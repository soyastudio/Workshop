package com.albertsons.esed.monitor.server;

public class DeploymentException extends RuntimeException {
    public DeploymentException() {
    }

    public DeploymentException(String message) {
        super(message);
    }

    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public DeploymentException(Throwable cause) {
        super(cause);
    }
}
