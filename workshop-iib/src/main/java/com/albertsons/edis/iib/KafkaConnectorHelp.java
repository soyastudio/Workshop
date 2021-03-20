package com.albertsons.edis.iib;

import com.ibm.broker.classloading.CacheSupportClassLoader;
import com.ibm.broker.connector.*;
import com.ibm.broker.connector.kafka.*;
import com.ibm.broker.plugin.MbBroker;
import com.ibm.broker.plugin.MbService;
import com.ibm.broker.util.IIBSecurityUtils;

public class KafkaConnectorHelp {

    public static void main(String[] args) {

        CredentialsStore credentialsStore;
        IIBLoginModule iibLoginModule;
        KafkaClassContext kafkaClassContext;
        ServiceTraceAppender serviceTraceAppender;
        KafkaConnectorUtils kafkaConnectorUtils;

        KafkaConnectorFactory factory;
        KafkaInputConnector kafkaInputConnector;
        KafkaIIBConsumer kafkaIIBConsumer;

        KafkaPollingResult kafkaPollingResult;
        KafkaOutputConnector outputConnector;
        KafkaOutputInteraction interaction;

        CacheSupportClassLoader cacheSupportClassLoader;

        AdminInterface adminInterface;
        ConnectorClassLoader connectorClassLoader;
        Connector connector;
        ContainerServices containerServices;
        ProviderLoader providerLoader;

        MbBroker mbBroker;
        MbService mbService;

    }

}
