package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.Pipeline;
import com.albertsons.esed.monitor.server.ServiceEventListener;
import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.RowSetDynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

//@Service
public class DataAccessService implements ServiceEventListener<DataAccessEvent> {
    static Logger logger = LoggerFactory.getLogger(DataAccessService.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ExecutorService executorService;

    @Subscribe
    public void onEvent(DataAccessEvent dataAccessEvent) {
        if (dataAccessEvent instanceof DataAccessEvent.SelectEvent) {
            DataAccessEvent.SelectEvent selectEvent = (DataAccessEvent.SelectEvent) dataAccessEvent;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    processSelectEvent(selectEvent);
                }
            });

        } else if(dataAccessEvent instanceof DataAccessEvent.UpdateEvent) {
            processUpdateEvent((DataAccessEvent.UpdateEvent) dataAccessEvent);
        }
    }

    private void processSelectEvent(DataAccessEvent.SelectEvent event) {

        JsonArray results = new JsonArray();
        String query = event.getQueries()[0];
        Connection connection = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            connection = dataSource.getConnection();

            st = connection.createStatement();
            rs = st.executeQuery(query);

            RowSetDynaClass rsdc = new RowSetDynaClass(rs);
            rsdc.getRows().forEach(e -> {
                JsonObject jsonObject = new JsonObject();
                DynaProperty[] properties = e.getDynaClass().getDynaProperties();
                for (DynaProperty property : properties) {
                    String name = property.getName();
                    String value = null;
                    try {
                        value = BeanUtils.getProperty(e, name);

                    } catch (IllegalAccessException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    } catch (InvocationTargetException invocationTargetException) {
                        invocationTargetException.printStackTrace();
                    } catch (NoSuchMethodException noSuchMethodException) {
                        noSuchMethodException.printStackTrace();
                    }

                    if (value != null) {
                        jsonObject.add(name, new JsonPrimitive(value));
                    }
                }
                results.add(jsonObject);

            });

            event.close(results);

        } catch (SQLException e) {
            event.close(e);

        } finally {
            close(rs);
            close(st);
            close(connection);
        }
    }

    private void processUpdateEvent(DataAccessEvent.UpdateEvent event) {
        executeBatchUpdate(event.getQueries());
    }

    private BatchUpdateResult executeBatchUpdate(String[] queries) {
        int result = 0;
        BatchUpdateResult updateResult = new BatchUpdateResult();

        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            int count = 0;
            List<String> list = new ArrayList<>();
            for (String query : queries) {
                list.add(query);
                statement.addBatch(query);

                count++;
                if (count % 30 == 0) {
                    int[] results = statement.executeBatch();
                    connection.commit();

                    for (int i = 0; i < results.length; i++) {
                        if (results[i] == 0) {
                            updateResult.getStatements().add(query);
                        } else {
                            updateResult.setUpdated(updateResult.getUpdated() + results[i]);
                        }
                    }

                    count = 0;
                    statement.clearBatch();
                    list.clear();
                }
            }

            if (count > 0) {
                int[] results = statement.executeBatch();
                connection.commit();

                for (int i = 0; i < results.length; i++) {
                    if (results[i] == 0) {
                        updateResult.getStatements().add(list.get(i));
                    } else {
                        updateResult.setUpdated(updateResult.getUpdated() + results[i]);
                    }
                }
            }

        } catch (SQLException e) {
            logger.error(e.getMessage());

        } finally {
            close(statement);
            close(connection);

        }

        return updateResult;
    }

    private void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
    }

    // ============================= to be removed:
    public PipelineResults init(Pipeline pipeline) {
        logger.info("initializing cache for: {}", pipeline.getBod().getName());
        PipelineResults results = new PipelineResults(pipeline);
        Connection connection = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            String query = new StringBuilder("SELECT * FROM ")
                    .append(pipeline.getSourceTable().getSchema())
                    .append(".")
                    .append(pipeline.getSourceTable().getTable())
                    .toString();

            st = connection.createStatement();
            rs = st.executeQuery(query);
            Pipeline.SourceTable sourceTable = pipeline.getSourceTable();
            if (sourceTable.metadata().isEmpty()) {
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    sourceTable.addTableColumn(Pipeline.TableColumn.newInstance()
                            .name(metaData.getColumnName(i))
                            .label(metaData.getColumnLabel(i))
                            .type(metaData.getColumnClassName(i))
                    );
                }
            }

            RowSetDynaClass rsdc = new RowSetDynaClass(rs);
            rsdc.getRows().forEach(e -> {
                results.getResults().add(PipelineUtils.toJsonObject(pipeline, e));
            });

            logger.info("Total {} records cached.", results.getResults().size());
            return results;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            close(rs);
            close(st);
            close(connection);
        }
    }

    public PipelineResults findAll(Pipeline pipeline) {
        String query = new StringBuilder("SELECT * FROM ")
                .append(pipeline.getSourceTable().getSchema())
                .append(".")
                .append(pipeline.getSourceTable().getTable())
                .toString();

        return select(pipeline, query);
    }

    public PipelineResults findById(Pipeline pipeline, String id) {
        String query = new StringBuilder("SELECT * FROM ")
                .append(pipeline.getSourceTable().getSchema())
                .append(".")
                .append(pipeline.getSourceTable().getTable())
                .append(" WHERE ")
                .append(pipeline.getSourceTable().getPrimaryKey())
                .append(" = '")
                .append(id)
                .append("'")
                .toString();

        return select(pipeline, query);
    }

    public PipelineResults search(Pipeline pipeline, String criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return findAll(pipeline);
        }

        String query = new StringBuilder("SELECT * FROM ")
                .append(pipeline.getSourceTable().getSchema())
                .append(".")
                .append(pipeline.getSourceTable().getTable())
                .append(" WHERE ")
                .append(criteria)
                .toString();

        return select(pipeline, query);
    }

    private PipelineResults select(Pipeline pipeline, String query) {
        PipelineResults results = new PipelineResults(pipeline);
        Connection connection = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            st = connection.createStatement();
            rs = st.executeQuery(query);
            Pipeline.SourceTable sourceTable = pipeline.getSourceTable();
            RowSetDynaClass rsdc = new RowSetDynaClass(rs);
            rsdc.getRows().forEach(e -> {
                results.getResults().add(PipelineUtils.toJsonObject(pipeline, e));
            });

            return results;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            close(rs);
            close(st);
            close(connection);
        }
    }

    public BatchUpdateResult insert(Pipeline pipeline, JsonArray array) {
        String[] inserts = PipelineUtils.createInsertStatement(pipeline, array);
        return executeBatchUpdate(inserts);
    }

    public BatchUpdateResult update(JsonArray array, Pipeline pipeline) {
        String[] updates = PipelineUtils.createUpdateStatement(pipeline, array);
        return executeBatchUpdate(updates);
    }

}
