package com.abs.uca.cfms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class ESED_CustomerPreferences_UCA_IH_Publisher_JavaCompute extends
        MbJavaComputeNode {

    private String bootstrap_servers = "dgv0137c6.safeway.com:9093,dgv0137be.safeway.com:9093,dgv0137c0.safeway.com:9093";

    public static final int TIMEOUT_DEFAULT_VALUE = 60000;
    public static final String ACKS_DEFAULT_VALUE = "0";
    public static final String SASL_MECHANISM_DEFAULT_VALUE = "UNAUTHENTICATED";
    public static final String SECURITY_PROTOCOL_DEFAULT_VALUE = "PLAINTEXT";
    public static final String SSL_PROTOCOL_DEFAULT_VALUE = "TLSv1.2";

    private String bootstrapServers;
    private String topicName;
    private String clientId;
    private String acks = "1";
    private int timeout = 60;
    private String securityProtocol = SECURITY_PROTOCOL_DEFAULT_VALUE;

    private Properties kafkaClientProducerConfigProps;

    @Override
    public void onInitialize() throws MbException {
        super.onInitialize();

        this.bootstrapServers = (String) this
                .getUserDefinedAttribute("bootstrapServers");
        this.topicName = (String) this.getUserDefinedAttribute("topicName");
        this.clientId = (String) getUserDefinedAttribute("clientId");
        this.acks = (String) this.getUserDefinedAttribute("acks");
        this.timeout = (int) this.getUserDefinedAttribute("timeout");
        this.securityProtocol = (String) this
                .getUserDefinedAttribute("securityProtocol");

        this.kafkaClientProducerConfigProps = new Properties();

        if (System.getenv("MQSI_KAFKA_PRODUCER_PROPERTIES_FILE") != null) {
            String propFile = System
                    .getenv("MQSI_KAFKA_PRODUCER_PROPERTIES_FILE");
            try {
                FileInputStream fis = new FileInputStream(new File(propFile));
                this.kafkaClientProducerConfigProps.load(new FileInputStream(
                        propFile));
                fis.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        this.kafkaClientProducerConfigProps.setProperty("bootstrap.servers",
                this.bootstrapServers);
        this.kafkaClientProducerConfigProps.setProperty("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        this.kafkaClientProducerConfigProps.setProperty("value.serializer",
                "org.apache.kafka.common.serialization.ByteArraySerializer");
        this.kafkaClientProducerConfigProps.setProperty("client.id",
                this.clientId);
        this.kafkaClientProducerConfigProps.setProperty("acks", this.acks);
        this.kafkaClientProducerConfigProps.setProperty("request.timeout.ms",
                Integer.toString(this.timeout));
        this.kafkaClientProducerConfigProps.setProperty("max.block.ms",
                Integer.toString(this.timeout));
        this.kafkaClientProducerConfigProps.setProperty("retries", "0");

        if ("SSL".equalsIgnoreCase(this.securityProtocol)) {
            String trustStoreType = "JKS";

            String trustStoreLocation = System
                    .getProperty("javax.net.ssl.trustStore");
            String trustStorePassword = System
                    .getProperty("javax.net.ssl.trustStorePassword");

            String keyStoreLocation = System
                    .getProperty("javax.net.ssl.keyStore");
            String keyStorePassword = trustStorePassword;

            this.kafkaClientProducerConfigProps.setProperty(
                    "security.protocol", "SSL");
            this.kafkaClientProducerConfigProps.setProperty(
                    "ssl.truststore.type", trustStoreType);

            this.kafkaClientProducerConfigProps.setProperty(
                    "ssl.truststore.location", trustStoreLocation);
            this.kafkaClientProducerConfigProps.setProperty(
                    "ssl.truststore.password", trustStorePassword);

            this.kafkaClientProducerConfigProps.setProperty(
                    "ssl.keystore.location", keyStoreLocation);

            //this.kafkaClientProducerConfigProps.setProperty("ssl.keystore.password", keyStorePassword);
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
            record.headers().add("Name", "Peter".getBytes());
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
        while(enumeration.hasMoreElements()) {
            String propName = (String) enumeration.nextElement();
            sb.append(propName).append(" = ").append(props.getProperty(propName)).append("\n");
        }

        throw new RuntimeException(sb.toString());
    }

}
