package com.albertsons.esed.monitor.server;

import java.io.File;

public abstract class PipelineEvent extends ServiceEvent {
    private final String pipeline;

    protected PipelineEvent(String pipeline) {
        super();
        this.pipeline = pipeline;
    }

    public String getPipeline() {
        return pipeline;
    }


    public static PipelineEvent createPipelineCreationEvent(File file) {
        return new PipelineCreationEvent(file.getParent(), file);
    }

    public static class PipelineCreationEvent extends PipelineEvent {
        private final File file;

        public PipelineCreationEvent(String pipeline, File file) {
            super(pipeline);
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }
}
