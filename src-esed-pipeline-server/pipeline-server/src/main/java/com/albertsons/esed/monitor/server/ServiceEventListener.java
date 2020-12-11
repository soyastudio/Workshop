package com.albertsons.esed.monitor.server;

public interface ServiceEventListener<E extends ServiceEvent> {
    void onEvent(E e);
}
