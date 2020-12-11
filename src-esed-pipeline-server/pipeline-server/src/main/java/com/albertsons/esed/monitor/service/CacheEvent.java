package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.ServiceEvent;

public abstract class CacheEvent extends ServiceEvent<Void> {
    private final String name;

    protected CacheEvent(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ReloadEvent reloadEvent(String name) {
        return new ReloadEvent(name);
    }

    public static SyncEvent syncEvent(String name) {
        return new SyncEvent(name);
    }

    public static class ReloadEvent extends CacheEvent {
        protected ReloadEvent(String name) {
            super(name);
        }
    }

    public static class SyncEvent extends CacheEvent {
        protected SyncEvent(String name) {
            super(name);
        }
    }
}
