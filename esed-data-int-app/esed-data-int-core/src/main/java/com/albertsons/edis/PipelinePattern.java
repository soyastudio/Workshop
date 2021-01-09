package com.albertsons.edis;

public interface PipelinePattern<T extends PipelineProcessor> {
    T build();
}
