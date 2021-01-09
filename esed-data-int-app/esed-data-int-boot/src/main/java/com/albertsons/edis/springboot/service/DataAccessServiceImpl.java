package com.albertsons.edis.springboot.service;

import com.albertsons.edis.DataAccessService;
import com.albertsons.edis.ServiceException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.RowSetDynaClass;

import javax.sql.DataSource;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataAccessServiceImpl implements DataAccessService {

    private DataSource dataSource;

    public DataAccessServiceImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public SqlBuilder sqlBuilder(String sql) throws ServiceException {
        try {
            return new DefaultSqlBuilder(dataSource.getConnection(), sql);

        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public BatchBuilder batchBuilder(String sql) {
        return null;
    }

    @Override
    public SelectResult search(SqlBuilder builder) throws ServiceException {
        try {
            return builder.executeQuery();

        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void update(SqlBuilder builder) throws ServiceException {
        try {
            builder.executeUpdate();
        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public void executeBatch(String[] queries) throws ServiceException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            int count = 0;
            for (String query : queries) {
                count++;
                statement.addBatch(query);

                System.out.println(query);

                if (count % 30 == 0 || count == queries.length) {
                    statement.executeBatch();
                }
            }

        } catch (SQLException e) {
            throw new ServiceException(e);

        } finally {
            close(statement);
            close(connection);

        }
    }

    @Override
    public void executeBatch(BatchBuilder builder) throws ServiceException {
        try {
            builder.execute();

        } catch (SQLException e) {
            throw new ServiceException(e);
        }
    }

    private static void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
    }

    static class DefaultSelectResult implements SelectResult {
        private final PropertyDescriptor[] properties;
        private final JsonArray data;

        private DefaultSelectResult(PropertyDescriptor[] properties, JsonArray data) {
            this.properties = properties;
            this.data = data;
        }

        public PropertyDescriptor[] getProperties() {
            return properties;
        }

        public JsonArray getData() {
            return data;
        }

        public static SelectResult fromResultSet(ResultSet resultSet) throws SQLException {
            JsonArray results = new JsonArray();
            RowSetDynaClass rsdc = new RowSetDynaClass(resultSet);
            PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(rsdc);
            DynaProperty[] properties = rsdc.getDynaProperties();
            rsdc.getRows().forEach(e -> {
                JsonObject jsonObject = new JsonObject();
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

            return new DefaultSelectResult(propertyDescriptors, results);
        }
    }

    static class DefaultSqlBuilder implements SqlBuilder {
        private Connection connection;
        private PreparedStatement statement;

        DefaultSqlBuilder(Connection connection, String sql) throws SQLException {
            this.connection = connection;
            this.statement = connection.prepareStatement(sql);
        }

        @Override
        public SqlBuilder set(int index, Object value, Class<?> type) throws SQLException {
            if (String.class.equals(type)) {
                statement.setString(index, (String) value);

            } else if (Number.class.isAssignableFrom(type)) {
                if (Integer.class.equals(type) || int.class.equals(type)) {
                    statement.setInt(index, (Integer) value);

                } else if (Long.class.equals(type) || long.class.equals(type)) {
                    statement.setLong(index, (Long) value);

                } else if (Short.class.equals(type) || short.class.equals(type)) {
                    statement.setShort(index, (Short) value);

                } else if (Double.class.equals(type) || double.class.equals(type)) {
                    statement.setDouble(index, (Double) value);

                } else if (Float.class.equals(type) || float.class.equals(type)) {
                    statement.setFloat(index, (Float) value);

                } else if (BigDecimal.class.isAssignableFrom(type)) {
                    statement.setBigDecimal(index, (BigDecimal) value);

                }
            } else if (Boolean.class.isAssignableFrom(type)) {
                statement.setBoolean(index, (Boolean) value);

            } else if (Date.class.isAssignableFrom(type)) {
                statement.setDate(index, (java.sql.Date) value);

            } else if (InputStream.class.isAssignableFrom(type)) {
                statement.setBlob(index, (InputStream) value);

            } else {
                statement.setObject(index, value);
            }

            return this;
        }

        @Override
        public SqlBuilder set(int index, Object value, Object defaultValue) throws SQLException {
            Class<?> type = defaultValue.getClass();
            Object v = value == null ? defaultValue : value;

            return set(index, v, type);
        }

        @Override
        public SqlBuilder set(String paramName, Object paramValue, Class<?> type) throws SQLException {
            return this;
        }

        @Override
        public SqlBuilder set(String paramName, Object paramValue, Object defaultValue) throws SQLException {
            return this;
        }

        public SelectResult executeQuery() throws SQLException {
            SelectResult result = null;
            ResultSet rs = null;
            try {
                rs = statement.executeQuery();
                return DefaultSelectResult.fromResultSet(rs);

            } catch (SQLException e) {
                throw e;

            } finally {
                close(rs);
                close(statement);
                close(connection);
            }

        }

        public void executeUpdate() throws SQLException {
            try {
                statement.executeUpdate();
            } catch (SQLException e) {
                throw e;

            } finally {
                close(statement);
                close(connection);
            }

        }
    }

    static class DefaultBatchBuilder implements BatchBuilder {
        private Connection connection;

        private int flushSize = 50;
        private String sql;
        private Class<?>[] parameterTypes = new Class[0];
        private Object[] defaultValues = new Object[0];
        private List<Object[]> paramList = new ArrayList();

        DefaultBatchBuilder(Connection connection) {
            this.connection = connection;
        }

        @Override
        public BatchBuilder flushSize(int flushSize) {
            this.flushSize = flushSize > 1 ? flushSize : 100;
            return this;
        }

        @Override
        public BatchBuilder statement(String sql) {
            this.sql = sql;
            return this;
        }

        @Override
        public BatchBuilder parameterTypes(Class<?>[] types) {
            this.parameterTypes = types;
            this.defaultValues = new Object[types.length];
            return this;
        }

        @Override
        public BatchBuilder parameterTypes(Class<?>[] types, Object[] defaultValues) {
            if (types.length != defaultValues.length) {
                throw new IllegalArgumentException("Array length not match.");
            }

            return this;
        }

        @Override
        public BatchBuilder addParameters(Object[] parameters) {
            if (parameters.length != parameterTypes.length) {
                throw new IllegalArgumentException("Array length not match.");
            }

            paramList.add(parameters);
            return this;
        }

        @Override
        public void execute() throws SQLException {
            PreparedStatement statement = null;
            try {
                connection.setAutoCommit(false);
                statement = connection.prepareStatement(sql);

                int count = 0;
                for (Object[] e : paramList) {
                    count++;
                    for (int i = 0; i < parameterTypes.length; i++) {
                        int index = i + 1;
                        Class<?> type = parameterTypes[i];
                        Object value = e[i];
                        if (String.class.equals(type)) {
                            statement.setString(index, (String) value);

                        } else if (Number.class.isAssignableFrom(type)) {
                            if (Integer.class.equals(type) || int.class.equals(type)) {
                                statement.setInt(index, (Integer) value);

                            } else if (Long.class.equals(type) || long.class.equals(type)) {
                                statement.setLong(index, (Long) value);

                            } else if (Short.class.equals(type) || short.class.equals(type)) {
                                statement.setShort(index, (Short) value);

                            } else if (Double.class.equals(type) || double.class.equals(type)) {
                                statement.setDouble(index, (Double) value);

                            } else if (Float.class.equals(type) || float.class.equals(type)) {
                                statement.setFloat(index, (Float) value);

                            } else if (BigDecimal.class.isAssignableFrom(type)) {
                                statement.setBigDecimal(index, (BigDecimal) value);

                            }
                        } else if (Boolean.class.isAssignableFrom(type)) {
                            statement.setBoolean(index, (Boolean) value);

                        } else if (Date.class.isAssignableFrom(type)) {
                            statement.setDate(index, (java.sql.Date) value);

                        } else if (InputStream.class.isAssignableFrom(type)) {
                            statement.setBlob(index, (InputStream) value);

                        } else {
                            statement.setObject(index, value);
                        }
                    }

                    statement.addBatch();
                    if (count % flushSize == 0 || count == paramList.size()) {
                        statement.executeUpdate();
                    }
                }

            } catch (SQLException ex) {
                throw ex;

            } finally {
                close(statement);
                close(connection);
            }
        }
    }
}
