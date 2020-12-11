package com.albertsons.esed.monitor.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ServiceEvent<T> {

    private final String id;
    private final long createdTime;

    private long endTime = -1l;
    private T value;
    private Exception exception;

    private ServiceEvent<?> parent;

    private transient Callback callback;
    private transient ExceptionHandler exceptionHandler;

    protected ServiceEvent() {
        this.id = UUID.randomUUID().toString();
        this.createdTime = System.currentTimeMillis();
    }

    protected ServiceEvent(ServiceEvent<?> parent) {
        this.id = UUID.randomUUID().toString();
        this.createdTime = System.currentTimeMillis();
        if (parent != null) {
            this.parent = parent;
        }
    }

    void callback(Callback callback, ExceptionHandler exceptionHandler) {
        this.callback = callback;
        this.exceptionHandler = exceptionHandler;
    }

    public ServiceEvent<?> getParent() {
        return parent;
    }

    public String getId() {
        return id;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public T getValue() {
        return value;
    }

    public Exception getException() {
        return exception;
    }

    public boolean isClosed() {
        return endTime > createdTime;
    }

    public synchronized void close() {
        if (isClosed()) {
            throw new IllegalStateException("Event already closed.");
        }

        this.endTime = System.currentTimeMillis();

        if(callback != null) {
            callback.onCompleted(this);
        }
    }

    public synchronized void close(T value) {
        if (isClosed()) {
            throw new IllegalStateException("Event already closed.");
        }

        this.value = value;
        this.endTime = System.currentTimeMillis();

        if(callback != null) {
            callback.onCompleted(this);
        }
    }

    public synchronized void close(Exception e) {
        if (isClosed()) {
            throw new IllegalStateException("Event already closed.");
        }

        this.exception = exception;
        this.endTime = System.currentTimeMillis();

        if(exceptionHandler != null) {
            exceptionHandler.onException(e);
        }
    }

    public static ServiceEvent fromJson(String json) {
        if (json == null)
            return null;

        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        if (jsonObject.get("type") != null) {
            try {
                Class<?> type = Class.forName(jsonObject.get("type").getAsString());
                Gson gson = new Gson();
                return (ServiceEvent) gson.fromJson(json, type);

            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Can not find the event type.");

        }
    }
}
