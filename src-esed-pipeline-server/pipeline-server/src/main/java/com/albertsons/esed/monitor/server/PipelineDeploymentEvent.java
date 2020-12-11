package com.albertsons.esed.monitor.server;

import java.io.File;

public class PipelineDeploymentEvent extends ServiceEvent {

    public static final String[] DEPLOYMENTS = new String[]{".zip"};
    public static final String START_EXTENSION = "start";
    public static final String STOP_EXTENSION = "stop";
    public static final String DELETE_EXTENSION = "delete";

    private final File file;

    public PipelineDeploymentEvent(File file) {
        super();
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public static PipelineDeploymentEvent createVerifyEvent(File file) {
        return new VerifyEvent(file);
    }

    public static PipelineDeploymentEvent createUndeployEvent(File file) {
        return new UndeployEvent(file);
    }

    public static PipelineDeploymentEvent createRedeployEvent(File file) {
        return new RedeployEvent(file);
    }

    public static class VerifyEvent extends PipelineDeploymentEvent {

        public VerifyEvent(File baseDir) {
            super(baseDir);
        }
    }

    static class UndeployEvent extends PipelineDeploymentEvent {

        UndeployEvent(File file) {
            super(file);
        }
    }

    static class RedeployEvent extends PipelineDeploymentEvent {

        RedeployEvent(File file) {
            super(file);
        }
    }
}
