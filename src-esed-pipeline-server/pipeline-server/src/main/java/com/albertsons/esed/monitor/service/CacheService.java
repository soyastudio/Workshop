package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.*;
import com.google.common.eventbus.Subscribe;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

//@Service
public class CacheService implements ServiceEventListener<CacheEvent> {
    static Logger logger = LoggerFactory.getLogger(CacheService.class);

    private ConcurrentHashMap<String, DefaultCache> caches = new ConcurrentHashMap<>();

    public Cache get(String name) {
        return caches.get(name);
    }

    public Cache create(String name, String[] columns, String keyColumn, String selectQuery, String insertQuery, String updateQuery, String deleteQuery) {
        if (caches.contains(name)) {
            throw new IllegalStateException("Cache '" + name + "' already exist.");
        }

        DefaultCache cache = new DefaultCache(name, columns, keyColumn, selectQuery, insertQuery, updateQuery, deleteQuery);
        caches.put(name, cache);

        return cache;
    }

    @PostConstruct
    public void init() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                caches.values().forEach(e -> {
                    if (e.changed && !e.locked) {
                        PipelineServer.getInstance().publish(CacheEvent.syncEvent(e.name));
                    }
                });
            }
        }, 10000l, 10000l);
    }

    @Subscribe
    public void onEvent(CacheEvent cacheEvent) {
        if (cacheEvent instanceof CacheEvent.ReloadEvent) {
            processReloadEvent((CacheEvent.ReloadEvent) cacheEvent);

        } else if (cacheEvent instanceof CacheEvent.SyncEvent) {
            processSyncEvent((CacheEvent.SyncEvent) cacheEvent);

        }
    }

    private void processReloadEvent(CacheEvent.ReloadEvent event) {
        logger.info("Reloading cache {}...", event.getName());

        DefaultCache cache = caches.get(event.getName());
        cache.locked = true;

        DataAccessEvent.SelectEvent selectEvent = DataAccessEvent.selectEvent(event, cache.selectQuery);
        PipelineServer.getInstance().publish(selectEvent, new Callback<DataAccessEvent.SelectEvent>() {
            @Override
            public void onCompleted(DataAccessEvent.SelectEvent event) {
                cache.reload(event.getValue());
                logger.info("Cache '" + cache.name + "' was initialized with " + cache.size() + "' records.");

            }
        }, null);
    }

    private void processSyncEvent(CacheEvent.SyncEvent event) {
        logger.info("processing Synchronization for cache {}...", event.getName());

        List<String> keys = new ArrayList<>();
        List<String> statements = new ArrayList<>();

        DefaultCache cache = caches.get(event.getName());
        cache.store.values().forEach(e -> {
            String stmt = null;
            if (e.isNew()) {
                keys.add(e.key);
                stmt = createSqlStatement(cache.insertQuery, cache.columns, e.getValue());

            } else if (e.isChanged()) {
                keys.add(e.key);
                stmt = createSqlStatement(cache.updateQuery, cache.columns, e.getValue());

            } else if (e.isRemoved()) {
                stmt = createSqlStatement(cache.deleteQuery, cache.columns, e.getOldValue());

            }

            if (stmt != null) {
                statements.add(stmt);
            }
        });

        System.out.println("========================= cache size: " + cache.size() + " changed: " + statements.size());

        PipelineServer.getInstance().publish(DataAccessEvent.updateEvent(event, statements.toArray(new String[statements.size()])), new Callback<DataAccessEvent.UpdateEvent>() {
            @Override
            public void onCompleted(DataAccessEvent.UpdateEvent event) {
                PipelineServer.getInstance().publish(CacheEvent.reloadEvent(cache.name));
            }
        }, new ExceptionHandler() {
            @Override
            public void onException(Exception e) {
                cache.locked = false;
            }
        });

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

    static class DefaultCache implements Cache {
        private final String name;

        private final String[] columns;
        private final String keyColumn;
        private final String selectQuery;
        private final String insertQuery;
        private final String updateQuery;
        private final String deleteQuery;

        private ConcurrentHashMap<String, Wrapper> store = new ConcurrentHashMap<>();
        private boolean changed;
        private boolean locked;

        DefaultCache(String name, String[] columns, String keyColumn, String selectQuery, String insertQuery, String updateQuery, String deleteQuery) {
            this.name = name;
            this.columns = columns;
            this.keyColumn = keyColumn;
            this.selectQuery = selectQuery;
            this.insertQuery = insertQuery;
            this.updateQuery = updateQuery;
            this.deleteQuery = deleteQuery;
            this.changed = true;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public synchronized void reload(JsonArray jsonArray) {
            store.clear();
            jsonArray.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                Wrapper w = new Wrapper(keyColumn, o);
                w.oldValue = o;
                store.put(w.getKey(), w);
            });

            this.changed = false;
            this.locked = false;

        }

        @Override
        public boolean contains(JsonObject value) {
            try {
                Wrapper w = new Wrapper(keyColumn, value);
                return store.contains(w.getKey());

            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public synchronized void put(JsonObject value) {
            Wrapper wrapper = new Wrapper(keyColumn, value);

            Wrapper item = store.get(wrapper.getKey());
            if(item != null) {
                item.oldValue = item.value;
                item.value = value;
                if(item.isChanged()) {
                    changed = true;
                    System.out.println("---------------- changed: " + wrapper.getKey());
                }

            } else {
                store.put(wrapper.getKey(), wrapper);
                changed = true;

                System.out.println("---------------- new: " + wrapper.getKey());
            }
        }

        @Override
        public JsonObject get(String key) {
            if (store.contains(key)) {
                return store.get(key).getValue();
            }

            return null;
        }

        @Override
        public int size() {
            return store.size();
        }

        @Override
        public boolean isChanged() {
            return this.changed;
        }
    }

    static class Wrapper {

        private final String key;
        private JsonObject value;
        private JsonObject oldValue;

        public Wrapper(String keyColumn, JsonObject value) {
            if (value != null && value.get(keyColumn) != null && value.get(keyColumn).isJsonPrimitive()) {
                this.key = value.get(keyColumn).getAsString();
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

        public boolean isChanged() {
            if(value == null || oldValue ==null) {
                return false;
            }

            return value.equals(oldValue);
        }
    }

}
