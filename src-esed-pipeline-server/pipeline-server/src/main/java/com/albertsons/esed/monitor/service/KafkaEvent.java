package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.ServiceEvent;

public abstract class KafkaEvent extends ServiceEvent<String> {
    private final String topicName;

    protected KafkaEvent(String topicName) {
        this.topicName = topicName;
    }

    protected KafkaEvent(ServiceEvent parent, String topicName) {
        super(parent);
        this.topicName = topicName;
    }

    public String getTopicName() {
        return topicName;
    }

    public static ProducerEvent producerEvent(ServiceEvent<?> parent,String topicName, String message) {
        return new ProducerEvent(parent, topicName, message);
    }


    public static class ProducerEvent extends KafkaEvent {

        private final String message;

        protected ProducerEvent(String topicName, String message) {
            super(topicName);
            this.message = message;
        }

        protected ProducerEvent(ServiceEvent parent, String topicName, String message) {
            super(parent, topicName);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ConsumerEvent extends KafkaEvent {
        protected ConsumerEvent(String topicName) {
            super(topicName);
        }

        protected ConsumerEvent(ServiceEvent parent, String topicName) {
            super(parent, topicName);
        }
    }
}
