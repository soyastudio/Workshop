package com.albertsons.esed.monitor.server;

@FunctionalInterface
public interface ExceptionHandler {
    void onException(Exception e);
}
