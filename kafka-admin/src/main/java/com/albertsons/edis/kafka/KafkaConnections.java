package com.albertsons.edis.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaConnections {

    private static Map<String, KafkaConnections> factories = new HashMap<>();

    private static Properties defaultProperties;
    private static Properties configuration;

    private String env;
    private Properties producerProperties;
    private Properties consumerProperties;
    private Properties adminProperties;
    private Properties streamProperties;

    static {
        try {
            defaultProperties = new Properties();

            // common
            defaultProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            defaultProperties.setProperty(CommonClientConfigs.CLIENT_ID_CONFIG, "test_client");
            defaultProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL);

            // producer
            defaultProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
            defaultProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");

            // consumer
            defaultProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test_group");
            defaultProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
            defaultProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
            defaultProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            configuration = new Properties(defaultProperties);
            InputStream inputStream = KafkaConnections.class.getClassLoader().getResourceAsStream("kafka-config.properties");
            configuration.load(inputStream);

        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }

    }

    protected static void configure(Properties properties) {
        configuration.putAll(properties);
        factories.clear();
    }

    private KafkaConnections(String env) {
        this.env = env;

        // Producer properties:
        this.producerProperties = new Properties();
        producerProperties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, getProperty(ProducerConfig.CLIENT_ID_CONFIG));
        producerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, getProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, getProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));

        producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        producerProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));

        if ("SSL".equalsIgnoreCase(producerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            producerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            producerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            producerProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            producerProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(producerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            producerProperties.setProperty(SaslConfigs.SASL_MECHANISM, getProperty(SaslConfigs.SASL_MECHANISM));
            producerProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        }

        // Consumer properties:
        this.consumerProperties = new Properties();

        consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, getProperty(ConsumerConfig.CLIENT_ID_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, getProperty(ConsumerConfig.GROUP_ID_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, getProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, getProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");

        consumerProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
        if ("SSL".equalsIgnoreCase(consumerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            consumerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            consumerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            consumerProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            consumerProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(consumerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            consumerProperties.setProperty(SaslConfigs.SASL_MECHANISM, getProperty(SaslConfigs.SASL_MECHANISM));
            consumerProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        }

        // Admin properties:
        this.adminProperties = new Properties();
        adminProperties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
        adminProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));

        if ("SSL".equalsIgnoreCase(adminProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            adminProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            adminProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            adminProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            adminProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(adminProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            adminProperties.setProperty(SaslConfigs.SASL_MECHANISM, getProperty(SaslConfigs.SASL_MECHANISM));
            adminProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        }

    }

    public KafkaProducer createKafkaProducer() {
        return new KafkaProducer(producerProperties);
    }

    public KafkaConsumer createKafkaConsumer() {
        return new KafkaConsumer(consumerProperties);
    }

    public AdminClient createAdminClient() {
        return AdminClient.create(adminProperties);
    }

    public static KafkaConnections getInstance(String env) {
        String name = env == null? "LOCAL" : env.toUpperCase();
        if(!factories.containsKey(name)) {
            factories.put(name, new KafkaConnections(name));
        }

        return factories.get(name);
    }

    private String getProperty(String propName) {
        if (System.getProperty("kafka." + propName) != null) {
            return System.getProperty("kafka." + propName);
        }

        String key = env + "." + propName;
        if (configuration.containsKey(key)) {
            return configuration.getProperty(key);
        }

        return defaultProperties.getProperty(propName);
    }
}
