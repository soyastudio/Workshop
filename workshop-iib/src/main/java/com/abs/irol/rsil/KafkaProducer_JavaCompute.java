package com.abs.irol.rsil;

import com.ibm.broker.javacompute.MbJavaComputeNode;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.errors.TimeoutException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.Future;

import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbUserException;

public class KafkaProducer_JavaCompute extends MbJavaComputeNode {

    private String bootstrapServers = "localhost:9092";
    private String topicName = "ESED_C01_PartnerRewardReconciliation";
    private String clientId = "EDIS_PARTNER_RECON";
    private String acks = "1";
    private String timeout = "60000";
    private String securityProtocol = "PLAINTEXT";
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

        this.kafkaClientProducerConfigProps = configureKafkaProducerProperties();
    }

    private Properties configureKafkaProducerProperties() {
        Properties configuration = new Properties();

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

        }

        return configuration;
    }

    public void evaluate(MbMessageAssembly inAssembly) throws MbException {

        MbMessage inMessage = inAssembly.getMessage();
        MbMessageAssembly outAssembly = null;

        Producer<String, byte[]> producer = new KafkaProducer<>(this.kafkaClientProducerConfigProps);
        try {
            // create new message as a copy of the input
            MbMessage outMessage = new MbMessage(inMessage);
            outAssembly = new MbMessageAssembly(inAssembly, outMessage);
            // ----------------------------------------------------------
            // Add user code below

            String msg = new String(inMessage.getBuffer());

            if(msg.indexOf("<?xml") > 0) {
                msg = msg.substring(msg.indexOf("<?xml"));
            }

            int begin = msg.indexOf("<Abs:RetailStoreId>");
            int end = msg.indexOf("</Abs:RetailStoreId>");
            String storeId = msg.substring(begin + "<Abs:RetailStoreId>".length(), end).trim();

            ProducerRecord<String, byte[]> record = new ProducerRecord<>(this.topicName, null, msg.getBytes());
            record.headers().add("StoreId", storeId.getBytes());

            long timestamp = System.currentTimeMillis();
            Future<RecordMetadata> future = producer.send(record);
            while (!future.isDone()) {
                if(System.currentTimeMillis() - timestamp > 60000L) {
                    throw new RuntimeException(new TimeoutException("Cannot publish message in 60 seconds."));
                }

                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            producer.flush();

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
            // Example handling ensures all exceptions are re-thrown to be handled in the flow
            throw new MbUserException(this, "evaluate()", "", "", e.toString(),
                    null);
        } finally {
            if (producer != null) {
                producer.close();
            }
        }
        // The following should only be changed
        // if not propagating message to the 'out' terminal
        // out.propagate(outAssembly);
    }

    public static byte[] asByteArray(Document doc, String encoding) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        return writer.getBuffer().toString().getBytes();
    }
}
