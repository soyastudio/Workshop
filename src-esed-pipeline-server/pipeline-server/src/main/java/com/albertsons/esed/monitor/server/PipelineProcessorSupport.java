package com.albertsons.esed.monitor.server;

import java.io.File;

public abstract class PipelineProcessorSupport<T> implements PipelineProcessor<T> {

    protected transient String bod;
    protected transient long timeout;
    protected transient File baseDir;
    protected transient PipelineContext pipelineContext;

    public void init(PipelineContext pipelineContext) {
        this.pipelineContext = pipelineContext;
        init();
    }

    protected abstract void init();

    protected void close(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                throw new RuntimeException(e);

            }
        }
    }

}
