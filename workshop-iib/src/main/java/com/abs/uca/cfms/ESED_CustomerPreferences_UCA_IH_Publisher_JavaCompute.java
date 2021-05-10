package com.abs.uca.cfms;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.*;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.config.SaslConfigs;

import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.Future;

public class ESED_CustomerPreferences_UCA_IH_Publisher_JavaCompute extends
        MbJavaComputeNode {

    private String bootstrapServers;
    private String topicName;
    private String clientId;
    private String acks = "1";
    private String timeout = "60000";
    private String securityProtocol = "SASL_SSL";
    private String saslMechanism = "SCRAM-SHA-512";
    private String scramLoginUsername = "";
    private String scramLoginPassword = "";

    private Properties kafkaClientProducerConfigProps;

    @Override
    public void onInitialize() throws MbException {
        super.onInitialize();

        this.bootstrapServers = (String) getUserDefinedAttribute("bootstrapServers");
        this.topicName = (String) getUserDefinedAttribute("topicName");
        this.clientId = (String) getUserDefinedAttribute("clientId");
        this.acks = (String) getUserDefinedAttribute("acks");
        this.timeout = (String) getUserDefinedAttribute("timeout");
        this.securityProtocol = (String) getUserDefinedAttribute("securityProtocol");
        this.saslMechanism = (String) getUserDefinedAttribute("saslMechanism");
        this.scramLoginUsername = (String) getUserDefinedAttribute("scramLoginUsername");
        this.scramLoginPassword = (String) getUserDefinedAttribute("scramLoginPassword");

        this.kafkaClientProducerConfigProps = new Properties();
        configureKafkaProducerProperties(kafkaClientProducerConfigProps);
    }

    private void configureKafkaProducerProperties(Properties configuration) {
        configuration.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configuration.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configuration.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        configuration.setProperty(ProducerConfig.CLIENT_ID_CONFIG, this.clientId);
        configuration.setProperty(ProducerConfig.ACKS_CONFIG, this.acks);
        configuration.setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, this.timeout);
        configuration.setProperty("max.block.ms", this.timeout);
        configuration.setProperty("retries", "0");

        configuration.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, this.securityProtocol);
        if ("SASL_SSL".equalsIgnoreCase(this.securityProtocol)) {
            configuration.setProperty(SaslConfigs.SASL_MECHANISM, this.saslMechanism);
            configuration.setProperty(SaslConfigs.SASL_JAAS_CONFIG,
                    new StringBuilder("org.apache.kafka.common.security.scram.ScramLoginModule")
                            .append(" required")
                            .append(" username=\"")
                            .append(scramLoginUsername)
                            .append("\"")
                            .append(" password=\"")
                            .append(scramLoginPassword)
                            .append("\";")
                            .toString());

        } else if ("SSL".equalsIgnoreCase(this.securityProtocol)) {

            String trustStoreType = "JKS";

            String trustStoreLocation = System
                    .getProperty("javax.net.ssl.trustStore");
            String trustStorePassword = System
                    .getProperty("javax.net.ssl.trustStorePassword");

            String keyStoreLocation = System
                    .getProperty("javax.net.ssl.keyStore");
            String keyStorePassword = trustStorePassword;

            configuration.setProperty(
                    "security.protocol", "SSL");
            configuration.setProperty(
                    "ssl.truststore.type", trustStoreType);

            configuration.setProperty(
                    "ssl.truststore.location", trustStoreLocation);
            configuration.setProperty(
                    "ssl.truststore.password", trustStorePassword);

            configuration.setProperty(
                    "ssl.keystore.location", keyStoreLocation);
            configuration.setProperty("ssl.keystore.password", keyStorePassword);
        }
    }

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {
        MbOutputTerminal out = getOutputTerminal("out");
        MbOutputTerminal alt = getOutputTerminal("alternate");

        //Properties sysProps = System.getProperties();
        //logProps(sysProps);

        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;

        Producer<String, byte[]> kafkaProducer = null;

        try {
            // create new message as a copy of the input
            MbMessage outMessage = new MbMessage(inMessage);
            outAssembly = new MbMessageAssembly(inAssembly, outMessage);
            // ----------------------------------------------------------
            // Add user code below

            ProducerRecord<String, byte[]> record = new ProducerRecord<>(
                    topicName, outAssembly.getMessage().getBuffer());
            record.headers().add("Name", "Bob".getBytes());
            record.headers().add("Occupation", "Builder".getBytes());

            kafkaProducer = new KafkaProducer<>(
                    this.kafkaClientProducerConfigProps);

            long timestamp = System.currentTimeMillis();
            Future<RecordMetadata> future = kafkaProducer.send(record);
            while (!future.isDone()) {
                if (System.currentTimeMillis() - timestamp > 2000l) {
                    throw new RuntimeException();
                }

                try {
                    Thread.sleep(100L);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            RecordMetadata metadata = future.get();

            //sb.append("metadata = ").append(metadata).append("\n");

            // End of user code
            // ----------------------------------------------------------
        } catch (MbException e) {
            // Re-throw to allow Broker handling of MbException
            throw e;
        } catch (RuntimeException e) {
            // Re-throw to allow Broker handling of RuntimeException
            throw e;
        } catch (Exception e) {
            // Consider replacing Exception with type(s) thrown by user code
            // Example handling ensures all exceptions are re-thrown to be
            // handled in the flow
            throw new MbUserException(this, "evaluate()", "", "", e.toString(),
                    null);
        } finally {
            if (kafkaProducer != null) {
                kafkaProducer.close();
            }
        }
        // The following should only be changed
        // if not propagating message to the 'out' terminal
        // out.propagate(outAssembly);
    }

    protected void logProps(Properties props) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append("MQSI_KAFKA_PRODUCER_PROPERTIES_FILE = ").append(System.getenv("MQSI_KAFKA_PRODUCER_PROPERTIES_FILE")).append("\n");

        Enumeration<?> enumeration = props.propertyNames();
        while (enumeration.hasMoreElements()) {
            String propName = (String) enumeration.nextElement();
            sb.append(propName).append(" = ").append(props.getProperty(propName)).append("\n");
        }

        throw new RuntimeException(sb.toString());
    }

}
