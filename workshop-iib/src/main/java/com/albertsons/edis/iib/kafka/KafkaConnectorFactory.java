package com.albertsons.edis.iib.kafka;

import com.ibm.broker.connector.ConnectorFactory;
import com.ibm.broker.connector.ContainerServices;
import com.ibm.broker.connector.InputConnector;
import com.ibm.broker.connector.OutputConnector;
import com.ibm.broker.connector.SecurityIdentity;
import com.ibm.broker.plugin.MbException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class KafkaConnectorFactory extends ConnectorFactory {
    private static final String className = "KafkaConnectorFactory";
    private String providerName;
    private Properties providerProps;

    public KafkaConnectorFactory() {
    }

    public void initialize(String var1, Properties var2) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaConnectorFactory", "initialize", "Properties: " + var2.toString());

        try {
            super.initialize(var1, var2);
            this.providerName = var1;
            if (var2 == null) {
                this.providerProps = new Properties();
            } else {
                this.providerProps = var2;
            }

            if (System.getProperty("broker.log4j.configuration") != null) {
                try {
                    PropertyConfigurator.configure(new URL(System.getProperty("broker.log4j.configuration")));
                } catch (MalformedURLException var7) {
                    var7.printStackTrace();
                }
            }

            Logger.getRootLogger().addAppender(new ServiceTraceAppender());
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaConnectorFactory", "initialize", "Exit");
        }

    }

    public OutputConnector createOutputConnector(String var1, Properties var2, SecurityIdentity var3) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaConnectorFactory", "createOutputConnector", "Properties: " + this.providerProps.toString());

        KafkaOutputConnector var4;
        try {
            var4 = new KafkaOutputConnector(this, var1, var2, var3);
        } catch (Exception var9) {
            ContainerServices.writeServiceTraceEntry("KafkaConnectorFactory", "createOutputConnector", "Caught exception constructing kafka output connector: " + var9.toString());
            throw var9;
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaConnectorFactory", "createOutputConnector", "Exit");
        }

        return var4;
    }

    public InputConnector createInputConnector(String var1, Properties var2, SecurityIdentity var3) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaConnectorFactory", "createInputConnector", "Properties: " + this.providerProps.toString());

        KafkaInputConnector var4;
        try {
            var4 = new KafkaInputConnector(this, var1, var2, var3);
        } catch (Exception var9) {
            ContainerServices.writeServiceTraceEntry("KafkaConnectorFactory", "createInputConnector", "Caught exception constructing kafka input connector: " + var9.toString());
            throw var9;
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaConnectorFactory", "createInputConnector", "Exit");
        }

        return var4;
    }

    public void terminate() throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaConnectorFactory", "terminate", "");
        ContainerServices.writeServiceTraceExit("KafkaConnectorFactory", "terminate", "Exit");
    }

    public String getInfo() {
        return "Kafka connector";
    }

    public Properties getProviderProperties() {
        return this.providerProps;
    }
}

