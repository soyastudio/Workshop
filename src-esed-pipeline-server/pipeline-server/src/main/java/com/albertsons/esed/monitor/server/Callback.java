package com.albertsons.esed.monitor.server;

@FunctionalInterface
public interface Callback<E extends ServiceEvent> {
    void onCompleted(E event);
}
