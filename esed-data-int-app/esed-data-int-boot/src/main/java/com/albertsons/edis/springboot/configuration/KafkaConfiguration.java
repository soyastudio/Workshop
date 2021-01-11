package com.albertsons.edis.springboot.configuration;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaAdmin;

//@Configuration
public class KafkaConfiguration {
    @Autowired
    private Environment environment;

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    KafkaProducer kafkaProducer() {
        return new KafkaProducer(kafkaProperties.buildProducerProperties());
    }

    @Bean
    KafkaConsumer kafkaConsumer() {
        return new KafkaConsumer(kafkaProperties.buildConsumerProperties());
    }

    @Bean
    AdminClient adminClient() {
        return AdminClient.create(kafkaProperties.buildAdminProperties());
    }
    
}
