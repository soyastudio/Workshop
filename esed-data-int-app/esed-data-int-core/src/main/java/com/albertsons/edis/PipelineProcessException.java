package com.albertsons.edis;

public class PipelineProcessException extends Exception {

    private final String pipeline;

    public PipelineProcessException(String pipeline, Throwable cause) {
        super(cause);
        this.pipeline = pipeline;
    }

    public String getPipeline() {
        return pipeline;
    }
}
