package com.albertsons.edis;

public interface PipelineProcessService {
    String[] list();

    void process(String pipeline);
}
