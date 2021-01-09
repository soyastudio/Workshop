package com.albertsons.edis;

public interface PipelineProcessor {
    PipelineContext getPipelineContext();

    void process() throws PipelineProcessException;

}
