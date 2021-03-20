package com.albertsons.edis.iib.kafka;

import com.ibm.broker.connector.ConnectorFactory;
import com.ibm.broker.connector.ContainerServices;
import com.ibm.broker.connector.OutputConnector;
import com.ibm.broker.connector.OutputInteraction;
import com.ibm.broker.connector.SecurityIdentity;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.testsupport.MbInternalSupport;
import com.ibm.broker.testsupport.MbInternalTestSupportListener;
import com.ibm.broker.testsupport.MbTestHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class KafkaOutputConnector extends OutputConnector implements MbInternalTestSupportListener {
    private static final String className = "KafkaOutputConnector";
    private Properties KafkaClientProducerConfigProps;
    private SecurityIdentity secIdentity;
    private boolean acksEnabled = false;
    private CredentialsStore.KafkaCredentials savedCredentials = null;
    public static final int TIMEOUT_DEFAULT_VALUE = 60000;
    public static final String ACKS_DEFAULT_VALUE = "0";
    public static final String SASL_MECHANISM_DEFAULT_VALUE = "UNAUTHENTICATED";
    public static final String SECURITY_PROTOCOL_DEFAULT_VALUE = "PLAINTEXT";
    public static final String SSL_PROTOCOL_DEFAULT_VALUE = "TLSv1.2";
    private String topicName;
    private String bootstrapServers;
    private int timeout;
    private String clientId;
    private String acks;
    private String saslMechanism;
    private String sslProtocol;
    private String securityProtocol;
    private boolean checkCredentialsDuringInitialize = false;
    private Properties providerProperties;

    public KafkaOutputConnector(ConnectorFactory var1, String var2, Properties var3, SecurityIdentity var4) throws MbException {
        super(var1, var2, var3);
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "KafkaOutputConnector <ctor>", "");
        MbInternalSupport.getInstance().register(this);
        this.updateInternalSupportParms();

        try {
            this.setTopicName(this.getProperties().getProperty("topicName"));
            this.setBootstrapServers(this.getProperties().getProperty("bootstrapServers"));
            this.setClientId(this.getProperties().getProperty("clientId"));
            this.setTimeout(this.getProperties().getProperty("timeout"));
            this.setAcks(this.getProperties().getProperty("acks"));
            this.setSaslMechanism(this.getProperties().getProperty("securityProtocol"));
            this.setSslProtocol(this.getProperties().getProperty("sslProtocol"));
            this.secIdentity = var4;
            this.providerProperties = ((KafkaConnectorFactory) var1).getProviderProperties();
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "KafkaOutputConnector <ctor>", this.providerProperties.toString());
        } catch (MbException var10) {
            throw var10;
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "KafkaOutputConnector <ctor>", "");
        }

    }

    public void updateInternalSupportParms() {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "updateInternalSupportParms", "");
        String var2 = MbInternalSupport.getInstance().getStringValue("/KafkaOutputConnector/checkCredentialsDuringInitialize");
        if (var2 != null && var2.equals("true")) {
            this.checkCredentialsDuringInitialize = true;
        } else {
            this.checkCredentialsDuringInitialize = false;
        }

        ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "updateInternalSupportParms", "checkCredentialsDuringInitialize=" + this.checkCredentialsDuringInitialize);
    }

    public void initialize() throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "initialize", "");
        super.initialize();

        try {
            this.KafkaClientProducerConfigProps = new Properties();
            String var2 = null;
            if (this.providerProperties.get("property1") != null) {
                var2 = (String) this.providerProperties.get("property1");
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "Properties file was set in provider properties under key 'property1': " + var2);
            }

            if (System.getenv("MQSI_KAFKA_PRODUCER_PROPERTIES_FILE") != null) {
                var2 = System.getenv("MQSI_KAFKA_PRODUCER_PROPERTIES_FILE");
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "Properties file was set in MQSI_KAFKA_PRODUCER_PROPERTIES_FILE: " + var2);
            }

            if (var2 != null) {
                try {
                    ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "loading the properties file " + var2);
                    File var3 = new File(var2);
                    FileInputStream var4 = new FileInputStream(var3);
                    this.KafkaClientProducerConfigProps.load(var4);
                    var4.close();
                } catch (FileNotFoundException var18) {
                    ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "Exception encountered while loading the properties file " + var2);
                    var18.printStackTrace();
                } catch (IOException var19) {
                    ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "Exception encountered while loading the properties file " + var2);
                    var19.printStackTrace();
                }
            }

            this.setTopicName(this.getProperties().getProperty("topicName"));
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "Topic: " + this.topicName);
            this.setBootstrapServers(this.getProperties().getProperty("bootstrapServers"));
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "boostrapServers: " + this.bootstrapServers);
            this.KafkaClientProducerConfigProps.setProperty("bootstrap.servers", this.bootstrapServers);
            String var22 = this.getProperties().getProperty("keySerializer");
            if (var22 == null) {
                if (this.KafkaClientProducerConfigProps.getProperty("key.serializer") != null) {
                    var22 = this.KafkaClientProducerConfigProps.getProperty("key.serializer");
                } else {
                    var22 = new String("org.apache.kafka.common.serialization.StringSerializer");
                }
            }

            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "keySerializer: " + var22);
            this.KafkaClientProducerConfigProps.setProperty("key.serializer", var22);
            String var23 = this.getProperties().getProperty("valueSerializer");
            if (var23 == null) {
                if (this.KafkaClientProducerConfigProps.getProperty("value.serializer") != null) {
                    var23 = this.KafkaClientProducerConfigProps.getProperty("value.serializer");
                } else {
                    var23 = new String("org.apache.kafka.common.serialization.ByteArraySerializer");
                }
            }

            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "valueSerializer: " + var23);
            this.KafkaClientProducerConfigProps.setProperty("value.serializer", var23);
            this.setClientId(this.getProperties().getProperty("clientId"));
            if (Boolean.parseBoolean(this.getProperties().getProperty("useClientIdSuffix"))) {
                this.clientId = KafkaConnectorUtils.buildClientId(this.clientId, (String) null, System.getProperty("broker.eglabel"), System.getProperty("broker.name"));
            }

            if (this.clientId != null) {
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "clientId: " + this.clientId);
                this.KafkaClientProducerConfigProps.setProperty("client.id", this.clientId);
            } else {
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "No clientId");
            }

            this.setAcks(this.getProperties().getProperty("acks"));
            if (this.acks.equals("0")) {
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "Publish acks disabled");
            } else if (this.acks.equals("1")) {
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "Publish acks set to 1");
                this.acksEnabled = true;
            } else if (this.acks.equals("all")) {
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "Publish acks set to 'all'");
                this.acksEnabled = true;
            } else {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"acks", this.acks});
            }

            this.KafkaClientProducerConfigProps.setProperty("acks", this.acks);
            this.setTimeout(this.getProperties().getProperty("timeout"));
            this.KafkaClientProducerConfigProps.put("request.timeout.ms", this.getTimeout());
            this.KafkaClientProducerConfigProps.put("max.block.ms", this.getTimeout());
            this.KafkaClientProducerConfigProps.setProperty("retries", "0");
            String var5 = this.getProperties().getProperty("securityProtocol");
            if (!this.KafkaClientProducerConfigProps.containsKey("sasl.mechanism")) {
                this.setSaslMechanism(var5);
                this.KafkaClientProducerConfigProps.setProperty("sasl.mechanism", this.saslMechanism);
            } else {
                this.saslMechanism = this.KafkaClientProducerConfigProps.getProperty("sasl.mechanism");
            }

            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "securityProtocol: " + this.securityProtocol + " SASLMechanism: " + this.saslMechanism);
            this.KafkaClientProducerConfigProps.setProperty("security.protocol", this.securityProtocol);
            this.setSslProtocol(this.getProperties().getProperty("sslProtocol"));
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "SSLProtocol: " + this.sslProtocol);
            this.KafkaClientProducerConfigProps.setProperty("ssl.enabled.protocols", this.sslProtocol);
            String var6 = null;
            String var7 = null;
            String var8 = System.getProperty("javax.net.ssl.trustStore");
            if (var8 == null) {
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "No javax.net.ssl.trustStore set");
            } else {
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "javax.net.ssl.trustStore: " + var8);
                var7 = System.getProperty("javax.net.ssl.trustStorePassword");
                if (var7 == null) {
                    ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "No javax.net.ssl.trustStorePassword set");
                } else {
                    ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "javax.net.ssl.trustStorePassword: xxxx");
                }

                var6 = this.getProperties().getProperty("ssl.truststore.type");
                if (var6 == null) {
                    var6 = new String("JKS");
                    ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "ssl.trustStore.type: JKS (default)");
                } else {
                    ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", "ssl.trustStore.type: " + var6);
                }

                this.KafkaClientProducerConfigProps.setProperty("ssl.truststore.location", var8);
                this.KafkaClientProducerConfigProps.setProperty("ssl.truststore.password", var7);
                this.KafkaClientProducerConfigProps.setProperty("ssl.truststore.type", var6);
            }

            Enumeration var9 = this.KafkaClientProducerConfigProps.keys();

            String var11;
            while (var9.hasMoreElements()) {
                String var10 = (String) var9.nextElement();
                var11 = this.KafkaClientProducerConfigProps.getProperty(var10);
                ContainerServices.writeServiceTraceData("KafkaOutputConnector", "initialize", var10 + ": " + var11);
            }

            CredentialsStore var24 = CredentialsStore.getInstance();

            try {
                this.savedCredentials = var24.saveCredentials(this.secIdentity);
            } catch (MbException var20) {
                if (!MbTestHelper.getInstance().isIgnoreJNILoadFailures()) {
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException(var20.getMessageKey(), (String[]) ((String[]) var20.getInserts()));
                } else {
                    System.out.println("Credential store cant store dummy credentials -- ignoring");
                }
            }

            if (this.checkCredentialsDuringInitialize) {
                var11 = var24.getUserName();
                if (var11 == null) {
                    var24.releaseCredentials(this.savedCredentials);
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("2110", new String[]{"username", "null"});
                }

                if (var11.length() == 0) {
                    var24.releaseCredentials(this.savedCredentials);
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("2112", new String[]{"username", "blank"});
                }

                String var12 = new String(var24.getPassword());
                if (var12 == null) {
                    var24.releaseCredentials(this.savedCredentials);
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("2111", new String[]{"password null", "username " + var11});
                }

                if (var12.length() == 0) {
                    var24.releaseCredentials(this.savedCredentials);
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("2113", new String[]{"password blank", "username " + var11});
                }
            }
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "initialize", "");
        }

    }

    public void terminate() throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "terminate", "");

        try {
            if (this.savedCredentials != null) {
                CredentialsStore var1 = CredentialsStore.getInstance();
                var1.releaseCredentials(this.savedCredentials);
                this.savedCredentials = null;
            }

            super.terminate();
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "terminate", "");
        }

    }

    public OutputInteraction createOutputInteraction() throws MbException {
        KafkaOutputInteraction var1 = null;
        ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "createOutputInteraction", "");

        try {
            var1 = new KafkaOutputInteraction(this);
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "createOutputInteraction", "");
        }

        return var1;
    }

    public String getTopicName() {
        return this.topicName;
    }

    public void setTopicName(String var1) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "setTopicName", var1);
        if (var1 == null) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3881", new String[]{"topicName"});
        } else if (var1.length() == 0) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"topicName", var1});
        }

        if (!KafkaConnectorUtils.validateTopicName(var1)) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"topicName", var1});
        }

        this.topicName = var1;
        ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "setTopicName", "");
    }

    public String getBootstrapServers() {
        return this.bootstrapServers;
    }

    public void setBootstrapServers(String var1) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "setBootstrapServers", var1);
        if (var1 == null) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3881", new String[]{"bootstrapServers"});
        } else if (var1.length() == 0) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"bootstrapServers", var1});
        }

        this.bootstrapServers = var1;
        ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "setBootstrapServers", "");
    }

    public String getClientId() {
        return this.clientId;
    }

    public void setClientId(String var1) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "setClientId", var1);
        if (var1 != null && !KafkaConnectorUtils.validateClientId(var1)) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"clientId", var1});
        }

        this.clientId = var1;
        ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "setClientId", "");
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(String var1) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "setTimeout", var1);
        int var2 = 60000;
        if (var1 == null) {
            var2 = 60000;
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "setTimeout", "timeout was not set. Setting timeout to use the default value: 60000");
        } else if (var1.length() == 0) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"timeout", var1});
        } else {
            try {
                var2 = Integer.parseInt(var1) * 1000;
                if (var2 <= 0) {
                    this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"timeout", var1});
                }
            } catch (NumberFormatException var4) {
                this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"timeout", var1});
            }
        }

        this.timeout = var2;
        ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "setTimeout", "");
    }

    public String getAcks() {
        return this.acks;
    }

    public void setAcks(String var1) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "setAcks", var1);
        if (var1 == null) {
            this.acks = "0";
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "setAcks", "acks was not set. Setting acks to use the default value: 0");
        } else if (var1.length() == 0) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"acks", var1});
        } else {
            this.acks = var1;
        }

        ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "setAcks", "");
    }

    public String getSaslMechanism() {
        return this.saslMechanism;
    }

    public void setSaslMechanism(String var1) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "setSaslMechanism", var1);
        if (var1 == null) {
            this.saslMechanism = "UNAUTHENTICATED";
            this.securityProtocol = "PLAINTEXT";
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "setSaslMechanism", "securityProtocol was not set. Setting saslMechanism to use the default value: UNAUTHENTICATED");
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "setSaslMechanism", "securityProtocol was not set. Setting securityProtocol to use the default value: PLAINTEXT");
        } else if (var1.length() == 0) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"securityProtocol", var1});
        } else if (var1.equals("PLAINTEXT")) {
            this.saslMechanism = "UNAUTHENTICATED";
            this.securityProtocol = var1;
        } else if (var1.equals("SSL")) {
            this.saslMechanism = "UNAUTHENTICATED";
            this.securityProtocol = var1;
        } else if (var1.equals("SASL_PLAINTEXT")) {
            this.saslMechanism = "PLAIN";
            this.securityProtocol = var1;
        } else if (var1.equals("SASL_SSL")) {
            this.saslMechanism = "PLAIN";
            this.securityProtocol = var1;
        } else {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"securityProtocol", var1});
        }

        if (System.getenv("MQSI_KAFKA_SASL_MECHANISM") != null) {
            this.saslMechanism = System.getenv("MQSI_KAFKA_SASL_MECHANISM");
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "setSaslMechanism", "env var is set overriding SASL mechanism to:" + this.saslMechanism);
        }

        ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "setSaslMechanism", "");
    }

    public String getSslProtocol() {
        return this.sslProtocol;
    }

    public void setSslProtocol(String var1) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputConnector", "setSslProtocol", var1);
        if (var1 == null) {
            this.sslProtocol = "TLSv1.2";
            ContainerServices.writeServiceTraceData("KafkaOutputConnector", "setSslProtocol", "sslProtocol was not set. Setting sslProtocol to use the default value: TLSv1.2");
        } else if (var1.length() == 0) {
            this.getConnectorFactory().getContainerServices().throwMbRecoverableException("3882", new String[]{"sslProtocol", var1});
        } else {
            this.sslProtocol = var1;
        }

        ContainerServices.writeServiceTraceExit("KafkaOutputConnector", "setSslProtocol", "");
    }

    public Properties getKafkaProperties() {
        return this.KafkaClientProducerConfigProps;
    }

    public boolean areAcksEnabled() {
        return this.acksEnabled;
    }

    public void commandFunction(String var1, String var2) {
    }

    public Properties getResolvedProperties() {
        return this.KafkaClientProducerConfigProps;
    }
}

