package com.albertsons.edis.kafka;

public class KafkaAdminException extends RuntimeException {
    public KafkaAdminException() {
    }

    public KafkaAdminException(String message) {
        super(message);
    }

    public KafkaAdminException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaAdminException(Throwable cause) {
        super(cause);
    }
}
