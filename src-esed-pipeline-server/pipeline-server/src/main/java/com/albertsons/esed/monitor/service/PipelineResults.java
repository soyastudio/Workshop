package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.Pipeline;
import com.google.gson.JsonArray;

public class PipelineResults {
    private Pipeline pipeline;
    private JsonArray results = new JsonArray();

    public PipelineResults(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public JsonArray getResults() {
        return results;
    }
}
