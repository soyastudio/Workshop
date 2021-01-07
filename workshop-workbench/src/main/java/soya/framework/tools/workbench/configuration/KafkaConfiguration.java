package soya.framework.tools.workbench.configuration;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import soya.framework.tools.workbench.kafka.KafkaAdminService;

@Configuration
public class KafkaConfiguration {
    @Autowired
    private Environment environment;

    @Autowired
    private KafkaProperties kafkaProperties;
    @Bean
    KafkaAdminService kafkaAdminService() {

        AdminClient adminClient = AdminClient.create(kafkaProperties.buildAdminProperties());
        KafkaProducer kafkaProducer = new KafkaProducer(kafkaProperties.buildProducerProperties());
        KafkaConsumer kafkaConsumer = new KafkaConsumer(kafkaProperties.buildConsumerProperties());

        return new KafkaAdminService(adminClient, kafkaProducer, kafkaConsumer);
    }
}
