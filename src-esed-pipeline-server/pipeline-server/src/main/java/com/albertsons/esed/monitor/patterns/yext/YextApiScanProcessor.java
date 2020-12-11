package com.albertsons.esed.monitor.patterns.yext;

import com.albertsons.esed.monitor.server.Callback;
import com.albertsons.esed.monitor.server.PipelineExecutionEvent;
import com.albertsons.esed.monitor.server.PipelineProcessorSupport;
import com.albertsons.esed.monitor.server.PipelineServer;
import com.albertsons.esed.monitor.service.Cache;
import com.albertsons.esed.monitor.service.CacheEvent;
import com.albertsons.esed.monitor.service.CacheService;
import com.albertsons.esed.monitor.service.HttpEvent;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

public class YextApiScanProcessor extends PipelineProcessorSupport {

    static Logger logger = LoggerFactory.getLogger(YextApiScanProcessor.class);

    private SourceTable sourceTable;
    private Api api;

    private transient State state = new State();
    private Cache cache;

    public State state() {
        return state;
    }

    public SourceTable getSourceTable() {
        return sourceTable;
    }

    public Api getApi() {
        return api;
    }

    @Override
    protected void init() {
        logger.info("initializing cache for: {}", bod);

        String query = new StringBuilder("SELECT * FROM ")
                .append(sourceTable.getSchema())
                .append(".")
                .append(sourceTable.getTable())
                .toString();
        cache = pipelineContext.getService(CacheService.class).create(bod, sourceTable.columns, sourceTable.primaryKey, query, sourceTable.insert, sourceTable.update, null);
        PipelineServer.getInstance().publish(CacheEvent.reloadEvent(bod));
    }

    @Override
    public void process(PipelineExecutionEvent event) {
        logger.info("scanning yext api for {}... ", bod);
        while (cache.isChanged()) {
            if (System.currentTimeMillis() - event.getCreatedTime() > timeout) {
                event.close(new TimeoutException("Timeout in processing pipeline: " + event.getPipeline()));
                return;
            }

            try {
                Thread.sleep(100l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        pipelineContext.getService(ExecutorService.class).execute(new Runnable() {
            @Override
            public void run() {
                String baseUrl = api.getUrl();

                JsonArray array = new JsonArray();
                int count = 0;
                while (!event.isClosed()) {
                    int offset = 50 * count;
                    String url = baseUrl.replace("{{OFFSET}}", "" + offset);

                    HttpEvent.Builder builder = HttpEvent.builder()
                            .parent(event)
                            .url(url)
                            .filter(api.filter);

                    if (api.shifters != null) {
                        for (Shifter shifter : api.shifters) {
                            builder.shifter(shifter.from, shifter.to, shifter.expression);
                        }
                    }

                    HttpEvent httpEvent = builder.create();

                    pipelineContext.getService(ExecutorService.class).execute(new Runnable() {
                        @Override
                        public void run() {
                            PipelineServer.getInstance().publish(httpEvent, new Callback<HttpEvent>() {
                                @Override
                                public void onCompleted(HttpEvent event) {

                                    String value = event.getValue();
                                    JsonArray results = parse(value);
                                    array.addAll(results);

                                    if (results.size() > 0) {
                                        results.forEach(e -> {
                                            cache.put(e.getAsJsonObject());
                                        });

                                    } else {
                                        PipelineExecutionEvent parent = (PipelineExecutionEvent) event.getParent();
                                        try {
                                            parent.close();

                                        } catch (IllegalStateException e) {

                                        }
                                    }
                                }
                            }, null);

                        }
                    });

                    try {
                        Thread.sleep(100l + 100l * count/20 );
                        count++;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                logger.info("Total {} results in {} call.", array.size(), count);
            }
        });

    }

    private JsonArray parse(String json) {
        JsonArray array = new JsonArray();
        if (json != null) {
            try {
                array = JsonParser.parseString(json).getAsJsonArray();

            } catch (JsonSyntaxException e) {
                logger.warn(e.getMessage());
            }
        }

        return array;

    }

    static class SourceTable {
        private String schema;
        private String table;
        private String primaryKey;
        private String[] columns;

        private String insert;
        private String update;

        private Map<String, TableColumn> metadata = new ConcurrentHashMap<>();

        public String getSchema() {
            return schema;
        }

        public String getTable() {
            return table;
        }

        public String getPrimaryKey() {
            return primaryKey;
        }

        public String[] getColumns() {
            return columns;
        }

        public String getInsert() {
            return insert;
        }

        public String getUpdate() {
            return update;
        }

        public Map<String, TableColumn> metadata() {
            return metadata;
        }

        public SourceTable addTableColumn(TableColumn column) {
            metadata.put(column.name.toUpperCase(), column);
            return this;
        }

        public TableColumn getTableColumn(String name) {
            return metadata.get(name.toUpperCase());
        }
    }

    static class Api {
        private String type = "single";
        private String url;
        private String filter;
        private Shifter[] shifters;

        public String getType() {
            return type;
        }

        public String getUrl() {
            return url;
        }

        public String getFilter() {
            return filter;
        }

        public Shifter[] getShifters() {
            return shifters;
        }
    }

    static class Shifter {
        private DataType type = DataType.String;
        private String from;
        private String to;
        private String expression;

        public DataType getType() {
            return type;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public String getExpression() {
            return expression;
        }
    }

    static class State {
        private boolean processing;
        private int round;
        private int cursor = 0;

        private State() {
        }

        public boolean isProcessing() {
            return processing;
        }

        public void setProcessing(boolean processing) {
            this.processing = processing;
        }

        public int getRound() {
            return round;
        }

        public int getCursor() {
            return cursor;
        }

        public State(int cursor) {
            this.cursor = cursor;
        }

        public int next() {
            return cursor++;
        }

        public void nextRound() {
            cursor = 0;
            round++;
        }
    }

    static class TableColumn {
        private String name;
        private String label;
        private String type;

        private TableColumn() {
        }

        public TableColumn name(String name) {
            this.name = name;
            return this;
        }

        public TableColumn label(String label) {
            this.label = label;
            return this;
        }

        public TableColumn type(String type) {
            this.type = type;
            return this;
        }

        public static TableColumn newInstance() {
            return new TableColumn();
        }
    }

    static enum DataType {
        String, Number, Boolean
    }

}
