package com.albertsons.edis.pipeline.yext;

import com.albertsons.edis.*;
import com.google.gson.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class YextApiScanComponent extends PipelineComponent<YextApiScanComponent.Processor> implements SchedulablePipeline {

    private static Logger logger = LoggerFactory.getLogger(YextApiScanComponent.class);
    private static Gson gson = new Gson();

    private SourceTable sourceTable;
    private Api api;

    public static class Processor extends PipelineComponent.Processor {
        private SourceTable sourceTable;
        private Api api;

        private Cache cache;
        private OkHttpClient httpClient;

        private boolean hasNextPage;

        @Override
        protected void init() {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .callTimeout(120, TimeUnit.SECONDS)
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);
            httpClient = builder.build();

            String query = new StringBuilder("SELECT * FROM ")
                    .append(sourceTable.getSchema())
                    .append(".")
                    .append(sourceTable.getTable())
                    .toString();

            cache = new Cache(pipeline, sourceTable.columns, sourceTable.primaryKey,
                    query, sourceTable.insert, sourceTable.update, null,
                    pipelineContext.getService(DataAccessService.class));

            new Thread(new Runnable() {
                @Override
                public void run() {
                    cache.reload();

                }
            }).start();
        }

        @Override
        protected void destroy() {
            super.destroy();
        }

        @Override
        public void process() throws PipelineProcessException {
            logger.info("Processing pipeline {}...", pipeline);

            if (cache.locked) {
                logger.info("wait until cache is unlocked...", pipeline);
            }

            long timestamp = System.currentTimeMillis();
            while (cache.locked) {
                if (System.currentTimeMillis() - timestamp > timeout) {
                    throw new PipelineProcessException(pipeline, new TimeoutException());
                }

                try {
                    Thread.sleep(100l);
                } catch (InterruptedException e) {
                    throw new PipelineProcessException(pipeline, e);
                }
            }

            String baseUrl = api.getUrl();
            logger.info("Scanning api from {}...", baseUrl);
            int count = 0;

            hasNextPage = true;
            while (hasNextPage) {
                int offset = 50 * count;
                String url = baseUrl.replace("{{OFFSET}}", "" + offset);
                Request request = new Request.Builder().url(url).build();
                Call call = httpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        hasNextPage = false;
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        String json = response.body().string();
                        if (isEmpty(json)) {
                            hasNextPage = false;

                        } else if (api.getFilter() != null) {
                            filterByJsonPath(api.getFilter(), json).forEach(e -> {
                                cache.put(shift(e.getAsJsonObject(), api.shifters));
                            });

                        } else {
                            filterByJsonPath(api.getJsonPath(), json).forEach(e -> {
                                cache.put(shift(e.getAsJsonObject(), api.shifters));
                            });
                        }

                    }
                });

                try {
                    Thread.sleep(100l + 100l * count / 20);
                    count++;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Wait until all requests finish
            try {
                Thread.sleep(3000l);
            } catch (InterruptedException e) {
                throw new PipelineProcessException(pipeline, e);
            }

            if (cache.changed) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cache.update();
                        cache.reload();
                    }
                }).start();

            } else {
                logger.info("{} API scan finished with no change.", pipeline);
            }

        }

        private boolean isEmpty(String json) {
            return filterByJsonPath(api.jsonPath, json).size() == 0;
        }

        private JsonArray filterByJsonPath(String jsonPath, String json) {
            Configuration JACKSON_JSON_NODE_CONFIGURATION = Configuration.builder().jsonProvider(new GsonJsonProvider())
                    .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS).build();

            Configuration conf = Configuration.builder().jsonProvider(new GsonJsonProvider())
                    .options(Option.ALWAYS_RETURN_LIST, Option.SUPPRESS_EXCEPTIONS).build();

            try {
                return JsonPath.using(conf).parse(json).read(jsonPath);

            } catch (Exception e) {
                return new JsonArray();
            }
        }

        private JsonObject shift(JsonObject src, Shifter[] shifters) {
            JsonObject target = new JsonObject();
            for (Shifter shifter : shifters) {
                String to = shifter.getTo();
                String from = shifter.getFrom();
                String exp = shifter.getExpression();

                JsonElement value = src.get(from);
                if (exp != null) {
                    value = Expressions.evaluate(value, exp);
                }

                target.add(to, value);
            }

            return target;
        }

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

    static class Api {
        private String url;
        private String jsonPath;
        private String filter;
        private Shifter[] shifters;

        public String getUrl() {
            return url;
        }

        public String getJsonPath() {
            return jsonPath;
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

    static enum DataType {
        String, Number, Boolean
    }

    static class Cache {
        private final String name;
        private final String[] columns;
        private final String keyColumn;

        private final String selectQuery;
        private final String insertQuery;
        private final String updateQuery;
        private final String deleteQuery;

        private DataAccessService dataAccessService;
        private Gson gson = new Gson();

        private ConcurrentHashMap<String, Wrapper> store = new ConcurrentHashMap<>();
        private boolean changed;
        private boolean locked;

        Cache(String name, String[] columns, String keyColumn, String selectQuery, String insertQuery, String updateQuery, String deleteQuery, DataAccessService dataAccessService) {
            this.name = name;
            this.columns = columns;
            this.keyColumn = keyColumn;
            this.selectQuery = selectQuery;
            this.insertQuery = insertQuery;
            this.updateQuery = updateQuery;
            this.deleteQuery = deleteQuery;
            this.dataAccessService = dataAccessService;
        }

        synchronized void reload() {
            logger.info("reloading cache {}...", name);
            locked = true;
            try {
                DataAccessService.SelectResult result = dataAccessService.search(dataAccessService.sqlBuilder(selectQuery));

                store.clear();
                result.getData().forEach(e -> {
                    JsonObject o = e.getAsJsonObject();
                    Wrapper w = new Wrapper(keyColumn, columns, o);
                    w.oldValue = o;
                    store.put(w.getKey(), w);
                });

                logger.info("cache {} reloaded with {} items.", name, store.size());
            } catch (ServiceException e) {
                throw e;

            } finally {
                this.locked = false;
                this.changed = false;
            }
        }

        synchronized void update() {
            logger.info("updating cache {}...", name);

            List<String> statements = new ArrayList<>();
            store.values().forEach(e -> {
                if (e.isNew()) {
                    statements.add(createSqlStatement(insertQuery, columns, e.getValue()));

                } else if (e.isRemoved()) {
                    statements.add(createSqlStatement(deleteQuery, columns, e.getOldValue()));

                } else if (e.isUpdated()) {
                    statements.add(createSqlStatement(updateQuery, columns, e.getValue()));

                }
            });

            if (statements.size() > 0) {
                locked = true;
                try {
                    dataAccessService.executeBatch(statements.toArray(new String[statements.size()]));
                    changed = false;

                    logger.info("cache {} have {} changed items, including new, delete and update.", name, statements.size());

                } catch (Exception e) {
                    throw new RuntimeException(e);

                } finally {
                    locked = false;
                }

            }

        }

        private String createSqlStatement(String template, String[] columns, JsonObject json) {
            if (template == null || json == null) {
                return null;
            }

            String result = template;
            for (String col : columns) {
                String token = ":" + col;
                JsonElement element = json.get(col);

                if (element == null || element.isJsonNull()) {
                    result = result.replaceAll(token, "null");

                } else if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = element.getAsJsonPrimitive();
                    String replacement = null;
                    if (primitive.isString()) {
                        replacement = primitive.getAsString();
                        replacement = "'" + escape(replacement) + "'";

                    } else if (primitive.isNumber()) {
                        replacement = "" + primitive.getAsNumber();

                    } else if (primitive.isBoolean()) {
                        replacement = "" + primitive.getAsBoolean();

                    }
                    result = result.replaceAll(token, replacement);

                } else {
                    String v = "'" + gson.toJson(element) + "'";
                    result = result.replaceAll(token, v);
                }
            }

            return result;
        }

        private String escape(String src) {
            String result = src;
            if (result.contains("'")) {
                result = result.replaceAll("'", "''");
            }

            return result;
        }

        public boolean contains(JsonObject value) {
            try {
                Wrapper w = new Wrapper(keyColumn, columns, value);
                return store.contains(w.getKey());

            } catch (Exception e) {
                return false;
            }
        }

        public synchronized void put(JsonObject value) {
            Wrapper wrapper = new Wrapper(keyColumn, columns, value);

            if (store.contains(wrapper.getKey())) {
                Wrapper item = store.get(wrapper.getKey());

                item.oldValue = item.value;
                item.value = value;
                if (item.isRemoved() || item.isUpdated()) {
                    System.out.println("---------------- changed: " + item.getKey());
                    changed = true;
                }

            } else {
                // new:
                store.put(wrapper.getKey(), wrapper);
                changed = true;
            }
        }

        public JsonObject get(String key) {
            if (store.contains(key)) {
                return store.get(key).getValue();
            }

            return null;
        }

        public int size() {
            return store.size();
        }

    }

    static class Wrapper {

        private final String key;
        private final String[] columns;
        private JsonObject value;
        private JsonObject oldValue;

        public Wrapper(String keyColumn, String[] columns, JsonObject value) {
            if (value != null && value.get(keyColumn) != null && value.get(keyColumn).isJsonPrimitive()) {
                this.key = value.get(keyColumn).getAsString();
                this.columns = columns;
                this.value = value;
                this.oldValue = null;

            } else {
                throw new IllegalArgumentException();
            }
        }

        public String getKey() {
            return key;
        }

        public JsonObject getValue() {
            return value;
        }

        public JsonObject getOldValue() {
            return oldValue;
        }

        public boolean isNew() {
            return value != null && oldValue == null;
        }

        public boolean isRemoved() {
            return value == null && oldValue != null;
        }

        public boolean isUpdated() {
            if (oldValue == null || value == null) {
                return true;
            }

            JsonObject a = new JsonObject();
            JsonObject b = new JsonObject();

            for (String col : columns) {
                a.add(col, oldValue.get(col));
                b.add(col, value.get(col));
            }

            return !gson.toJson(a).equals(gson.toJson(b));
        }


    }

}
