package com.albertsons.esed.monitor.server;

public interface PipelineContext {
    <T> T getService(Class<T> serviceType);
}
