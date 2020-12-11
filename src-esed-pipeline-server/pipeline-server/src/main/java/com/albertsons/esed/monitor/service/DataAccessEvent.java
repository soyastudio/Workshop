package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.ServiceEvent;
import com.google.gson.JsonArray;

public abstract class DataAccessEvent<T> extends ServiceEvent<T> {

    private final String[] queries;
    private T result;
    private Exception exception;

    protected DataAccessEvent(ServiceEvent<?> parent, String[] queries) {
        this.queries = queries;
    }

    public String[] getQueries() {
        return queries;
    }

    public static SelectEvent selectEvent(ServiceEvent<?> parent, String query) {
        return new SelectEvent(parent, new String[]{query});
    }

    public static UpdateEvent updateEvent(ServiceEvent<?> parent, String[] queries) {
        return new UpdateEvent(parent, queries);
    }

    public static class SelectEvent extends DataAccessEvent<JsonArray> {
        protected SelectEvent(ServiceEvent<?> parent, String[] queries) {
            super(parent, queries);
        }
    }

    public static class UpdateEvent extends DataAccessEvent {
        protected UpdateEvent(ServiceEvent<?> parent, String[] queries) {
            super(parent, queries);
        }
    }

}
