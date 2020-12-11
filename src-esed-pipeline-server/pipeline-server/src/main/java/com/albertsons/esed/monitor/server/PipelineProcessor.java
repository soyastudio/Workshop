package com.albertsons.esed.monitor.server;

public interface PipelineProcessor<T> {
    void init(PipelineContext pipelineContext);

    void process(PipelineExecutionEvent event);
}
