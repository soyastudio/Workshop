package com.albertsons.edis.iib.kafka;

import com.ibm.broker.activitylog.ActivityLog;
import com.ibm.broker.activitylog.ActivityLog.NODETYPE;
import com.ibm.broker.activitylog.ActivityLog.RM;
import com.ibm.broker.connector.ContainerServices;
import com.ibm.broker.connector.OutputInteraction;
import com.ibm.broker.connector.OutputRecord;
import com.ibm.broker.plugin.MbException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class KafkaOutputInteraction extends OutputInteraction {
    private static final String className = "KafkaOutputInteraction";
    private KafkaProducer kafkaProducer = null;
    private boolean neverConnected = true;
    private KafkaOutputConnector connector;

    KafkaOutputInteraction(KafkaOutputConnector var1) throws MbException {
        super(var1);
        ContainerServices.writeServiceTraceEntry("KafkaOutputInteraction", "constructor", "");

        try {
            this.connector = var1;
            this.initialiseKafkaProducer();
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaOutputInteraction", "constructor", "");
        }

    }

    public void terminate() throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputInteraction", "terminate", "");

        try {
            this.closeKafkaProducer();
            super.terminate();
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaOutputInteraction", "terminate", "");
        }

    }

    public Properties send(Properties var1, OutputRecord var2) throws MbException {
        Properties var3 = null;
        String var4 = var1.getProperty("topicName");
        if (var4 == null) {
            var4 = ((KafkaOutputConnector) this.getConnector()).getTopicName();
            ContainerServices.writeServiceTraceEntry("KafkaOutputInteraction", "send", "Publishing to node topic: " + var4);
        } else {
            ContainerServices.writeServiceTraceEntry("KafkaOutputInteraction", "send", "Publishing to topic: " + var4);
        }

        String var5 = var1.getProperty("key");

        try {
            if (this.kafkaProducer == null) {
                this.initialiseKafkaProducer();
            }

            var3 = new Properties();
            ProducerRecord var6 = null;
            if (var5 == null) {
                var6 = new ProducerRecord(var4, var2.getByteData());
            } else {
                ContainerServices.writeServiceTraceData("KafkaOutputInteraction", "send", "Message key: " + var5);
                var6 = new ProducerRecord(var4, var5, var2.getByteData());
            }

            RecordMetadata var22 = (RecordMetadata) this.kafkaProducer.send(var6).get();
            if (var22 != null) {
                this.neverConnected = false;
                Integer var23 = var22.partition();
                String var24 = "";
                if (var23 != -1) {
                    String var10 = Integer.toString(var23);
                    var3.setProperty("partition", var10);
                    if (((KafkaOutputConnector) this.getConnector()).areAcksEnabled()) {
                        var24 = Long.toString(var22.offset());
                        var3.setProperty("offset", var24);
                        ContainerServices.writeServiceTraceData("KafkaOutputInteraction", "send", "Message has been sent to parition " + var10 + " at offset " + var24);
                    } else {
                        ContainerServices.writeServiceTraceData("KafkaOutputInteraction", "send", "Message has been sent to parition " + var10);
                    }

                    String var11 = var22.topic();
                    String var14 = ((KafkaOutputConnector) this.getConnector()).getClientId();
                    ActivityLog.writeTraceEvent(this, 1073754893L, new String[]{var11, var10, var24, var14}).connector(((KafkaOutputConnector) this.getConnector()).getName()).resourceManager(RM.Kafka).nodetype(NODETYPE.OUTPUT).end();
                }

                var3.setProperty("topicName", var22.topic());
                if (var5 != null) {
                    var3.setProperty("key", var5);
                }

                var3.setProperty("checksum", Long.toString(var22.checksum()));
            }
        } catch (MbException var19) {
            ContainerServices.writeServiceTraceData("KafkaOutputInteraction", "send", "MbException: " + var19.toString());
            throw var19;
        } catch (Exception var20) {
            ContainerServices.writeServiceTraceData("KafkaOutputInteraction", "send", "Failed to send message: " + var20.toString());
            this.closeKafkaProducer();
            String var7;
            if (this.neverConnected) {
                var7 = "3893";
            } else {
                var7 = "3894";
            }

            String var8 = var20.getMessage();
            String[] var9 = new String[]{var4, this.getConnector().getProperties().getProperty("bootstrapServers"), var8};
            this.connector.getConnectorFactory().getContainerServices().writeSystemLogError(var7, var9);
            this.connector.getConnectorFactory().getContainerServices().throwMbRecoverableException(var7, var9);
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaOutputInteraction", "send", "");
        }

        return var3;
    }

    private void initialiseKafkaProducer() throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaOutputInteraction", "initialiseKafkaProducer", "");
        this.kafkaProducer = null;

        try {
            KafkaClassContext var1 = KafkaClassContext.switchTo(this);
            Throwable var2 = null;

            try {
                try {
                    KafkaOutputConnector var3 = (KafkaOutputConnector) this.getConnector();
                    this.kafkaProducer = new KafkaProducer(var3.getKafkaProperties());
                } catch (ExceptionInInitializerError var27) {
                    Throwable var32 = var27.getCause();
                    Throwable var33 = var27.getException();
                    String var6 = "";
                    if (var33 != null) {
                        var6 = var33.getMessage();
                        ContainerServices.writeServiceTraceData("KafkaOutputInteraction", "constructor", "Failed to create Kafka producer. base: " + var33.toString());
                    }

                    if (var32 != null) {
                        var6 = var33.getMessage();
                        ContainerServices.writeServiceTraceData("KafkaOutputInteraction", "constructor", "Failed to create Kafka producer. cause: " + var32.toString());
                    }

                    String var7 = "3895";
                    String[] var8 = new String[]{var6};
                    this.connector.getConnectorFactory().getContainerServices().writeSystemLogError(var7, var8);
                    this.connector.getConnectorFactory().getContainerServices().throwMbRecoverableException(var7, var8);
                } catch (Throwable var28) {
                    ContainerServices.writeServiceTraceData("KafkaOutputInteraction", "constructor", "Failed to create Kafka producer. base: " + var28.toString());
                    String var4 = "3895";
                    String[] var5 = new String[]{var28.getMessage()};
                    this.connector.getConnectorFactory().getContainerServices().writeSystemLogError(var4, var5);
                    this.connector.getConnectorFactory().getContainerServices().throwMbRecoverableException(var4, var5);
                }
            } catch (Throwable var29) {
                var2 = var29;
                throw var29;
            } finally {
                if (var1 != null) {
                    if (var2 != null) {
                        try {
                            var1.close();
                        } catch (Throwable var26) {
                            var2.addSuppressed(var26);
                        }
                    } else {
                        var1.close();
                    }
                }

            }
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaOutputInteraction", "initialiseKafkaProducer", "Succesfully initialised KafkaProducer");
        }

    }

    private void closeKafkaProducer() {
        ContainerServices.writeServiceTraceEntry("KafkaOutputInteraction", "closeKafkaProducer", "");

        try {
            if (this.kafkaProducer != null) {
                this.kafkaProducer.close();
                this.kafkaProducer = null;
            }
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaOutputInteraction", "closeKafkaProducer", "");
        }

    }
}
