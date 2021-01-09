package com.albertsons.edis;

public interface PipelineDeployer {

    String[] pipelineNames();

    Pipeline getPipeline(String name);

    PipelineProcessor getPipelineProcessor(String name);

}
