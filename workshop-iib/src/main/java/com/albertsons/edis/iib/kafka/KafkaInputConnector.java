package com.albertsons.edis.iib.kafka;

import com.ibm.broker.activitylog.ActivityLog;
import com.ibm.broker.activitylog.ActivityLog.NODETYPE;
import com.ibm.broker.activitylog.ActivityLog.RM;
import com.ibm.broker.connector.ConnectorFactory;
import com.ibm.broker.connector.ContainerServices;
import com.ibm.broker.connector.PollingInputConnector;
import com.ibm.broker.connector.PollingResult;
import com.ibm.broker.connector.SecurityIdentity;
import com.ibm.broker.connector.TimeoutPollingResult;
import com.ibm.broker.plugin.MbException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.kafka.common.KafkaException;

public class KafkaInputConnector extends PollingInputConnector {
    private static final String className = "KafkaInputConnector";
    private Properties KafkaClientConsumerConfigProps;
    private KafkaIIBConsumer kafkaConsumer = null;
    private String topicName = "";
    private String clientId = "";
    private String groupId = "";
    private boolean commitOffsetToKafkaProperty = true;
    private SecurityIdentity secIdentity;
    private CredentialsStore.KafkaCredentials savedCredentials = null;
    private Properties providerProperties;
    public static final String ENABLE_AUTO_COMMIT_DEFAULT_VALUE = "true";
    public static final String INITIAL_OFFSET_DEFAULT_VALUE = "latest";
    public static final int CONNECTION_TIMEOUT_DEFAULT_VALUE = 15000;
    public static final int SESSION_TIMEOUT_DEFAULT_VALUE = 10000;
    public static final int RECEIVE_BATCH_SIZE_DEFAULT_VALUE = 1;
    public static final String SECURITY_PROTOCOL_DEFAULT_VALUE = "PLAINTEXT";
    public static final String SASL_MECHANISM_DEFAULT_VALUE = "UNAUTHENTICATED";
    public static final String SSL_PROTOCOL_DEFAULT_VALUE = "TLSv1.2";

    public KafkaInputConnector(ConnectorFactory var1, String var2, Properties var3, SecurityIdentity var4) throws MbException {
        super(var1, var2, var3);
        ContainerServices.writeServiceTraceEntry("KafkaInputConnector", "KafkaInputConnector <ctor>", "");
        String var6 = "";
        this.providerProperties = ((KafkaConnectorFactory) var1).getProviderProperties();

        try {
            this.KafkaClientConsumerConfigProps = new Properties();
            String var7 = null;
            if (this.providerProperties.get("property2") != null) {
                var7 = (String) this.providerProperties.get("property2");
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "KafkaInputConnector <ctor>", "Properties file was set in provider properties under key 'property2': " + var7);
            }

            if (System.getenv("MQSI_KAFKA_CONSUMER_PROPERTIES_FILE") != null) {
                var7 = System.getenv("MQSI_KAFKA_CONSUMER_PROPERTIES_FILE");
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "KafkaInputConnector <ctor>", "Properties file was set in MQSI_KAFKA_CONSUMER_PROPERTIES_FILE: " + var7);
            }

            if (var7 != null) {
                try {
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "loading the properties file " + var7);
                    File var8 = new File(var7);
                    FileInputStream var9 = new FileInputStream(var8);
                    this.KafkaClientConsumerConfigProps.load(var9);
                    var9.close();
                } catch (FileNotFoundException var36) {
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "Exception encountered while loading the properties file " + var7);
                    var36.printStackTrace();
                } catch (IOException var37) {
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "Exception encountered while loading the properties file " + var7);
                    var37.printStackTrace();
                }
            }

            this.topicName = this.getProperties().getProperty("topicName");
            if (this.topicName == null) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3881", new String[]{"topicName"});
            } else if (this.topicName.length() == 0) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"topicName", this.topicName});
            }

            if (!com.ibm.broker.connector.kafka.KafkaConnectorUtils.validateTopicName(this.topicName)) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"topicName", this.topicName});
            }

            ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "Topic: " + this.topicName);
            var6 = this.getProperties().getProperty("bootstrapServers");
            if (var6 == null) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3881", new String[]{"bootstrapServers"});
            } else if (var6.length() == 0) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"bootstrapServers", var6});
            }

            ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "BoostrapServers: " + var6);
            this.KafkaClientConsumerConfigProps.setProperty("bootstrap.servers", var6);
            String var39 = this.getProperties().getProperty("keyDeserializer");
            if (var39 == null) {
                if (this.KafkaClientConsumerConfigProps.getProperty("key.deserializer") != null) {
                    var39 = this.KafkaClientConsumerConfigProps.getProperty("key.deserializer");
                } else {
                    var39 = new String("org.apache.kafka.common.serialization.StringDeserializer");
                }
            }

            ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "keyDeserializer: " + var39);
            this.KafkaClientConsumerConfigProps.setProperty("key.deserializer", var39);
            String var40 = this.getProperties().getProperty("valueDeserializer");
            if (var40 == null) {
                if (this.KafkaClientConsumerConfigProps.getProperty("value.deserializer") != null) {
                    var40 = this.KafkaClientConsumerConfigProps.getProperty("value.deserializer");
                } else {
                    var40 = new String("org.apache.kafka.common.serialization.ByteArrayDeserializer");
                }
            }

            ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "valueDeserializer: " + var40);
            this.KafkaClientConsumerConfigProps.setProperty("value.deserializer", var40);
            this.clientId = this.getProperties().getProperty("clientId");
            if (this.clientId != null && !com.ibm.broker.connector.kafka.KafkaConnectorUtils.validateClientId(this.clientId)) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"clientId", this.clientId});
            }

            if (Boolean.parseBoolean(this.getProperties().getProperty("useClientIdSuffix"))) {
                this.clientId = com.ibm.broker.connector.kafka.KafkaConnectorUtils.buildClientId(this.clientId, (String) null, System.getProperty("broker.eglabel"), System.getProperty("broker.name"));
            }

            if (this.clientId != null) {
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "clientId: " + this.clientId);
                this.KafkaClientConsumerConfigProps.setProperty("client.id", this.clientId);
            } else {
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "No clientId");
            }

            this.groupId = this.getProperties().getProperty("groupId");
            if (this.groupId == null) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3881", new String[]{"groupId"});
            } else if (this.groupId.length() == 0) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"groupId", this.groupId});
            }

            if (this.groupId != null) {
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "groupId: " + this.groupId);
                if (!KafkaConnectorUtils.validateGroupId(this.groupId)) {
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"groupId", this.groupId});
                }

                this.KafkaClientConsumerConfigProps.setProperty("group.id", this.groupId);
            }

            this.KafkaClientConsumerConfigProps.setProperty("enable.auto.commit", "false");
            String var10 = this.getProperties().getProperty("enableAutoCommit");
            if (var10 == null) {
                var10 = "true";
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "enableAutoCommit was not set. Setting enableAutoCommit to use the default value: true");
            } else if (var10.length() == 0) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"enableAutoCommit", var10});
            }

            if (!var10.equals("false") && !var10.equals("true")) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"enableAutoCommit", var10});
            } else if (var10.equals("false")) {
                this.commitOffsetToKafkaProperty = false;
            } else {
                this.commitOffsetToKafkaProperty = true;
            }

            String var11 = this.getProperties().getProperty("initialOffset");
            if (var11 == null) {
                var11 = "latest";
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "initialOffset was not set. Setting initialOffset to use the default value: latest");
            } else if (var11.length() == 0) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"initialOffset", var11});
            }

            if (!var11.equals("latest") && !var11.equals("earliest")) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"initialOffset", var11});
            } else {
                this.KafkaClientConsumerConfigProps.setProperty("auto.offset.reset", var11);
            }

            String var12 = this.getProperties().getProperty("connectionTimeout");
            int var13 = 15000;
            if (var12 == null) {
                var13 = 15000;
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "connectionTimeout was not set. Setting connectionTimeout to use the default value: 15000");
            } else if (var12.length() == 0) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"connectionTimeout", var12});
            } else {
                try {
                    var13 = Integer.parseInt(var12) * 1000;
                    if (var13 <= 0) {
                        this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"connectionTimeout", var12});
                    }
                } catch (NumberFormatException var35) {
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"connectionTimeout", var12});
                }
            }

            this.KafkaClientConsumerConfigProps.put("request.timeout.ms", var13);
            String var14 = this.getProperties().getProperty("sessionTimeout");
            int var15 = 10000;
            if (var14 == null) {
                var15 = 10000;
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "sessionTimeout was not set. Setting sessionTimeout to use the default value: 10000");
            } else if (var14.length() == 0) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"sessionTimeout", var14});
            } else {
                try {
                    var15 = Integer.parseInt(var14) * 1000;
                    if (var15 < 10000) {
                        this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"sessionTimeout", var14});
                    }
                } catch (NumberFormatException var34) {
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"sessionTimeout", var14});
                }
            }

            if (var15 >= var13) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3898", new String[]{var14, var12});
            }

            this.KafkaClientConsumerConfigProps.put("session.timeout.ms", var15);
            this.KafkaClientConsumerConfigProps.put("heartbeat.interval.ms", var15 - 1);
            String var16 = this.getProperties().getProperty("receiveBatchSize");
            int var17 = 1;
            if (var16 == null) {
                var17 = 1;
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "receiveBatchSize was not set. Setting receiveBatchSize to use the default value: 1");
            } else if (var16.length() == 0) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"receiveBatchSize", var16});
            } else {
                try {
                    var17 = Integer.parseInt(var16);
                    if (var17 < 1) {
                        this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"receiveBatchSize", var16});
                    }
                } catch (NumberFormatException var33) {
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"receiveBatchSize", var16});
                }
            }

            this.KafkaClientConsumerConfigProps.put("max.poll.records", var17);
            String var18 = this.getProperties().getProperty("securityProtocol");
            String var19;
            if (!this.KafkaClientConsumerConfigProps.containsKey("sasl.mechanism")) {
                var19 = "UNAUTHENTICATED";
                if (var18 == null) {
                    var19 = "UNAUTHENTICATED";
                    var18 = "PLAINTEXT";
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "securityProtocol was not set. Setting saslMechanism to use the default value: UNAUTHENTICATED");
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "securityProtocol was not set. Setting securityProtocol to use the default value: PLAINTEXT");
                } else if (var18.length() == 0) {
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"securityProtocol", var18});
                } else if (var18.equals("PLAINTEXT")) {
                    var19 = "UNAUTHENTICATED";
                } else if (var18.equals("SSL")) {
                    var19 = "UNAUTHENTICATED";
                } else if (var18.equals("SASL_PLAINTEXT")) {
                    var19 = "PLAIN";
                } else if (var18.equals("SASL_SSL")) {
                    var19 = "PLAIN";
                } else {
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"securityProtocol", var18});
                }

                if (System.getenv("MQSI_KAFKA_SASL_MECHANISM") != null) {
                    var19 = System.getenv("MQSI_KAFKA_SASL_MECHANISM");
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "env var is set overriding SASL mechanism to:" + var19);
                }

                this.KafkaClientConsumerConfigProps.setProperty("security.protocol", var18);
                this.KafkaClientConsumerConfigProps.setProperty("sasl.mechanism", var19);
            }

            ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "securityProtocol: " + var18 + " SASLMechanism: " + this.KafkaClientConsumerConfigProps.getProperty("sasl.mechanism"));
            var19 = this.getProperties().getProperty("sslProtocol");
            if (var19 == null) {
                var19 = "TLSv1.2";
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "sslProtocol was not set. Setting sslProtocol to use the default value: TLSv1.2");
            } else if (var19.length() == 0) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"sslProtocol", var19});
            }

            ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "SSLProtocol: " + var19);
            this.KafkaClientConsumerConfigProps.setProperty("ssl.enabled.protocols", var19);
            String var20 = null;
            String var21 = null;
            String var22 = System.getProperty("javax.net.ssl.trustStore");
            if (var22 == null) {
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "No javax.net.ssl.trustStore set");
            } else {
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "javax.net.ssl.trustStore: " + var22);
                var21 = System.getProperty("javax.net.ssl.trustStorePassword");
                if (var21 == null) {
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "No javax.net.ssl.trustStorePassword set");
                } else {
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "javax.net.ssl.trustStorePassword: xxxx");
                }

                var20 = this.getProperties().getProperty("ssl.truststore.type");
                if (var20 == null) {
                    var20 = new String("JKS");
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "ssl.trustStore.type: JKS (default)");
                } else {
                    ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", "ssl.trustStore.type: " + var20);
                }

                this.KafkaClientConsumerConfigProps.setProperty("ssl.truststore.location", var22);
                this.KafkaClientConsumerConfigProps.setProperty("ssl.truststore.password", var21);
                this.KafkaClientConsumerConfigProps.setProperty("ssl.truststore.type", var20);
            }

            this.secIdentity = var4;
            Enumeration var23 = this.KafkaClientConsumerConfigProps.keys();

            while (var23.hasMoreElements()) {
                String var24 = (String) var23.nextElement();
                String var25 = this.KafkaClientConsumerConfigProps.getProperty(var24);
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "initialize", var24 + ": " + var25);
            }
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaInputConnector", "KafkaInputConnector", "");
        }

    }

    public void initialize() throws MbException {
        super.initialize();
        ContainerServices.writeServiceTraceEntry("KafkaInputConnector", "initialize", "");
        CredentialsStore var1 = CredentialsStore.getInstance();

        try {
            try {
                this.savedCredentials = var1.saveCredentials(this.secIdentity);
            } catch (MbException var7) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException(var7.getMessageKey(), (String[]) ((String[]) var7.getInserts()));
            }

            this.kafkaConsumer = new KafkaIIBConsumer(this.topicName, this.KafkaClientConsumerConfigProps, this.getConnectorFactory().getContainerServices(), this);
        } catch (MbException var8) {
            if (this.savedCredentials != null) {
                var1.releaseCredentials(this.savedCredentials);
                this.savedCredentials = null;
            }

            throw var8;
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaInputConnector", "initialize", "");
        }

    }

    public KafkaIIBConsumer getKafkaConsumer() {
        return this.kafkaConsumer;
    }

    public void stop() throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaInputConnector", "terminate", "");

        try {
            this.kafkaConsumer.stop();
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaInputConnector", "stop", "");
        }

    }

    public void terminate() throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaInputConnector", "terminate", "");

        try {
            this.kafkaConsumer.stop();

            try {
                this.kafkaConsumer.close();
            } catch (Exception var6) {
                ContainerServices.writeServiceTraceEntry("KafkaInputConnector", "terminate", "Exception on close: " + var6.toString());
            }

            if (this.savedCredentials != null) {
                CredentialsStore var1 = CredentialsStore.getInstance();
                var1.releaseCredentials(this.savedCredentials);
                this.savedCredentials = null;
            }
        } catch (Exception var7) {
            ContainerServices.writeServiceTraceEntry("KafkaInputConnector", "terminate", "Exception on terminate: " + var7.toString());
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaInputConnector", "terminate", "");
        }

    }

    public void start() throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaInputConnector", "start", "");

        try {
            this.kafkaConsumer.start();
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaInputConnector", "start", "");
        }

    }

    public PollingResult readData(long var1) throws MbException {
        Object var3 = null;
        ContainerServices.writeServiceTraceEntry("KafkaInputConnector", "readData", "");

        try {
            String var5;
            try {
                var3 = this.kafkaConsumer.poll(var1);
                if (var3 == null) {
                    var3 = new TimeoutPollingResult(this);
                } else {
                    String var4 = ((com.ibm.broker.connector.kafka.KafkaPollingResult) var3).getTopic();
                    var5 = Integer.valueOf(((com.ibm.broker.connector.kafka.KafkaPollingResult) var3).getPartition()).toString();
                    String var17 = Long.valueOf(((com.ibm.broker.connector.kafka.KafkaPollingResult) var3).getOffset()).toString();
                    String var7 = ((KafkaPollingResult) var3).getKey();
                    String var8 = this.clientId;
                    String var9 = this.groupId;
                    ActivityLog.writeTraceEvent(this, 1073754892L, new String[]{var4, var5, var17, var8, var9}).connector(this.getName()).resourceManager(RM.Kafka).nodetype(NODETYPE.INPUT).end();
                    if (var7 != null) {
                        ContainerServices.writeServiceTraceData("KafkaInputConnector", "buildProperties", "topic: " + var4 + " partiton: " + var5 + " offset: " + var17 + " key: " + var7);
                    } else {
                        ContainerServices.writeServiceTraceData("KafkaInputConnector", "buildProperties", "topic: " + var4 + " partiton: " + var5 + " offset: " + var17);
                    }
                }
            } catch (MbException var14) {
                var3 = new TimeoutPollingResult(this, 10000L);
            } catch (KafkaException var15) {
                ContainerServices.writeServiceTraceData("KafkaInputConnector", "readData", "Failed to read data from kafka " + var15.toString());
                var5 = "3892";
                String[] var6 = new String[]{this.topicName, var15.getMessage()};
                this.getConnectorFactory().getContainerServices().writeSystemLogError(var5, var6);
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException(var5, var6);
                var3 = new TimeoutPollingResult(this);
            }
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaInputConnector", "readData", "");
        }

        return (PollingResult) var3;
    }

    public boolean commitOffsetToKafka() {
        return this.commitOffsetToKafkaProperty;
    }

    public Properties getResolvedProperties() {
        return this.KafkaClientConsumerConfigProps;
    }
}

