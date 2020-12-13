package com.albertsons.esed.monitor.service;

import com.albertsons.esed.monitor.server.ServiceEventListener;
import com.google.common.eventbus.Subscribe;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

//@Service
public class KafkaService implements ServiceEventListener<KafkaEvent> {
    @Autowired
    private KafkaProperties kafkaProperties;

    private KafkaProducer producer;
    private KafkaConsumer consumer;

    @PostConstruct
    public void init() {
        producer = new KafkaProducer(kafkaProperties.buildProducerProperties());
        consumer = new KafkaConsumer(kafkaProperties.buildConsumerProperties());
    }

    @Subscribe
    public void onEvent(KafkaEvent kafkaEvent) {

    }
}
