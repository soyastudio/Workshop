package com.albertsons.edis;

public interface PipelineContext {
    <T> T getService(Class<T> serviceType);
}
