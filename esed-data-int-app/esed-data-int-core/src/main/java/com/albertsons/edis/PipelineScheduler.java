package com.albertsons.edis;

public interface PipelineScheduler {
    void schedule(PipelineProcessor processor, String name, String calendar, long delay);

    void unschedule(String pipeline);
}
