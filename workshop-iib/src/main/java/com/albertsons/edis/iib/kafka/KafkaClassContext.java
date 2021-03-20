package com.albertsons.edis.iib.kafka;

import java.io.Closeable;

public class KafkaClassContext implements Closeable {
    private final ClassLoader previous = Thread.currentThread().getContextClassLoader();

    public static KafkaClassContext switchToClassLoader(ClassLoader var0) {
        return new KafkaClassContext(var0);
    }

    public static KafkaClassContext switchTo(Object var0) {
        return switchToClassLoader(var0.getClass().getClassLoader());
    }

    private KafkaClassContext(ClassLoader var1) {
        Thread.currentThread().setContextClassLoader(var1);
    }

    public void close() {
        Thread.currentThread().setContextClassLoader(this.previous);
    }
}
