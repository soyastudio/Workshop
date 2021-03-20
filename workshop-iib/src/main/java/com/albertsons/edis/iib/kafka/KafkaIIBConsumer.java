package com.albertsons.edis.iib.kafka;

import com.ibm.broker.connector.ContainerServices;
import com.ibm.broker.connector.PollingInputConnector;
import com.ibm.broker.plugin.MbException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

public class KafkaIIBConsumer {
    private static final String className = "KafkaIIBConsumer";
    private ContainerServices containerServices;
    private KafkaConsumer<byte[], byte[]> kafkaConsumer = null;
    private Properties properties;
    private String topicName;
    private String clientId;
    private PollingInputConnector inputConnector;
    private boolean commitMessageOffsetToKafka;
    private boolean firstTime = true;
    private String lastMsgKey = null;
    private long lastMsgTime = 0L;
    private int secondsUntilNextMessage = 0;
    private boolean stopping = false;
    private LinkedList<KafkaPollingResult> receivedMessagesList;

    public KafkaIIBConsumer(String var1, Properties var2, ContainerServices var3, KafkaInputConnector var4) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaIIBConsumer", "constructor", "");

        try {
            KafkaClassContext var5 = KafkaClassContext.switchTo(this);
            Throwable var6 = null;

            try {
                this.properties = var2;
                this.containerServices = var3;
                this.topicName = var1;
                this.inputConnector = var4;
                this.commitMessageOffsetToKafka = var4.commitOffsetToKafka();
                this.clientId = var2.getProperty("client.id");
                if (this.clientId == null) {
                    this.clientId = "";
                }

                this.receivedMessagesList = new LinkedList();
            } catch (Throwable var22) {
                var6 = var22;
                throw var22;
            } finally {
                if (var5 != null) {
                    if (var6 != null) {
                        try {
                            var5.close();
                        } catch (Throwable var21) {
                            var6.addSuppressed(var21);
                        }
                    } else {
                        var5.close();
                    }
                }

            }
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaIIBConsumer", "constructor", "");
        }

    }

    private void initialise() throws MbException {
        if (this.kafkaConsumer == null) {
            ContainerServices.writeServiceTraceEntry("KafkaIIBConsumer", "initialise", "");

            try {
                String var2;
                String[] var3;
                String var4;
                String var5;
                Throwable var26;
                try {
                    this.kafkaConsumer = new KafkaConsumer(this.properties);
                } catch (ExceptionInInitializerError var19) {
                    Throwable var24 = var19.getCause();
                    var26 = var19.getException();
                    var4 = "";
                    if (var26 != null) {
                        var4 = var26.getMessage();
                        ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Failed to create Kafka consumer. base: " + var26.toString());
                    }

                    if (var24 != null) {
                        var4 = var24.getMessage();
                        ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Failed to create Kafka consumer. cause: " + var24.toString());
                    }

                    var5 = "3890";
                    String[] var6 = new String[]{var4, this.clientId};
                    this.writeRepeatingErrorToSyslog(var5, var6);
                    this.containerServices.throwMbRecoverableException(var5, var6);
                } catch (Throwable var20) {
                    ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Failed to create Kafka consumer.: " + var20.toString());
                    var20.printStackTrace();
                    if (var20.getCause() != null) {
                        var20.getCause().printStackTrace();
                    }

                    var2 = "3890";
                    var3 = new String[]{var20.getMessage(), this.clientId};
                    this.writeRepeatingErrorToSyslog(var2, var3);
                    this.containerServices.throwMbRecoverableException(var2, var3);
                }

                List var1 = null;

                String var27;
                String[] var28;
                Throwable var29;
                String var30;
                try {
                    var1 = this.kafkaConsumer.partitionsFor(this.topicName);
                } catch (ExceptionInInitializerError var21) {
                    var26 = var21.getCause();
                    var29 = var21.getException();
                    var5 = "";
                    if (var29 != null) {
                        var5 = var29.getMessage();
                        ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Failed to retrieve meta data for topic(" + this.topicName + "). base: " + var5);
                    }

                    if (var26 != null) {
                        var5 = var26.getMessage();
                        ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Failed to retrieve meta data for topic(" + this.topicName + "). cause: " + var5);
                    }

                    this.kafkaConsumer = null;
                    var30 = "3891";
                    String[] var7 = new String[]{this.topicName, this.properties.getProperty("bootstrap.servers"), var5};
                    this.writeRepeatingErrorToSyslog(var30, var7);
                    this.containerServices.throwMbRecoverableException(var30, var7);
                } catch (Throwable var22) {
                    var27 = var22.getMessage();
                    ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Failed to retrieve meta data for topic: " + var27);
                    this.kafkaConsumer = null;
                    var4 = "3891";
                    var28 = new String[]{this.topicName, this.properties.getProperty("bootstrap.servers"), var27};
                    this.writeRepeatingErrorToSyslog(var4, var28);
                    this.containerServices.throwMbRecoverableException(var4, var28);
                }

                if (var1 == null || var1.isEmpty()) {
                    ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Topic list is empty");
                    this.kafkaConsumer = null;
                    var2 = "3899";
                    var3 = new String[]{this.topicName};
                    this.writeRepeatingErrorToSyslog(var2, var3);
                    this.containerServices.throwMbRecoverableException(var2, var3);
                }

                ArrayList var25 = new ArrayList();
                var25.add(this.topicName);

                try {
                    this.kafkaConsumer.subscribe(var25);
                    if (!this.firstTime) {
                        var27 = "3887";
                        String[] var31 = new String[]{this.clientId, this.topicName};
                        this.containerServices.writeSystemLogError(var27, var31);
                    } else {
                        this.firstTime = false;
                    }

                    this.secondsUntilNextMessage = 0;
                } catch (ExceptionInInitializerError var17) {
                    var29 = var17.getCause();
                    Throwable var32 = var17.getException();
                    var30 = "";
                    if (var32 != null) {
                        var30 = var32.getMessage();
                        ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Failed to subscribe to topic. base: " + var32.toString());
                    }

                    if (var29 != null) {
                        var30 = var29.getMessage();
                        ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Failed to subscribe to topic. cause: " + var29.toString());
                    }

                    this.kafkaConsumer = null;
                    String var33 = "3892";
                    String[] var8 = new String[]{this.topicName, this.properties.getProperty("bootstrap.servers"), var30};
                    this.writeRepeatingErrorToSyslog(var33, var8);
                    this.containerServices.throwMbRecoverableException(var33, var8);
                } catch (Throwable var18) {
                    ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "initialise", "Failed to subscribe to topic. base: " + var18.toString());
                    this.kafkaConsumer = null;
                    var4 = "3892";
                    var28 = new String[]{this.topicName, this.properties.getProperty("bootstrap.servers"), var18.getMessage()};
                    this.writeRepeatingErrorToSyslog(var4, var28);
                    this.containerServices.throwMbRecoverableException(var4, var28);
                }
            } finally {
                if (this.kafkaConsumer == null) {
                    ContainerServices.writeServiceTraceExit("KafkaIIBConsumer", "initialise", "Failed to initialise KafkaConsumer");
                } else {
                    ContainerServices.writeServiceTraceExit("KafkaIIBConsumer", "initialise", "Succesfully initialised KafkaConsumer");
                }

            }

        }
    }

    public synchronized KafkaPollingResult poll(long var1) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaIIBConsumer", "poll", "");
        KafkaPollingResult var3 = null;
        boolean var4 = true;

        try {
            KafkaClassContext var5 = KafkaClassContext.switchTo(this);
            Throwable var44 = null;

            try {
                while (true) {
                    while (var3 == null && var4 && !this.stopping) {
                        var4 = false;
                        this.initialise();
                        var3 = (KafkaPollingResult) this.receivedMessagesList.poll();
                        if (var3 != null) {
                            if (this.commitMessageOffsetToKafka) {
                                try {
                                    this.commitMessageOffset(var3.getPartition(), var3.getOffset());
                                    ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "poll", "Delivering message from previous batch " + var3.getPartition() + ":" + var3.getOffset());
                                } catch (MbException var37) {
                                    var3 = null;
                                    var4 = true;
                                }
                            } else {
                                ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "poll", "Delivering message from previous batch " + var3.getPartition() + ":" + var3.getOffset());
                            }
                        } else {
                            ConsumerRecords var45 = this.kafkaConsumer.poll(var1);
                            ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "poll", "Received batch of " + var45.count() + " messages.");
                            Iterator var8 = var45.iterator();
                            int var9 = 0;

                            while (var8.hasNext()) {
                                ConsumerRecord var10 = (ConsumerRecord) var8.next();
                                if (var9++ == 0) {
                                    var3 = new KafkaPollingResult(this.inputConnector, var10);
                                    if (this.commitMessageOffsetToKafka) {
                                        try {
                                            this.commitMessageOffset(var3.getPartition(), var3.getOffset());
                                            ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "poll", "Delivering first message in batch " + var10.partition() + ":" + var10.offset());
                                        } catch (MbException var36) {
                                            var3 = null;
                                            var4 = true;
                                        }
                                    } else {
                                        ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "poll", "Delivering first message in batch " + var10.partition() + ":" + var10.offset());
                                    }
                                } else {
                                    ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "poll", "Queueing batch message " + var10.partition() + ":" + var10.offset());
                                    this.receivedMessagesList.add(new KafkaPollingResult(this.inputConnector, var10));
                                }
                            }
                        }
                    }

                    return var3;
                }
            } catch (Throwable var38) {
                var44 = var38;
                throw var38;
            } finally {
                if (var5 != null) {
                    if (var44 != null) {
                        try {
                            var5.close();
                        } catch (Throwable var35) {
                            var44.addSuppressed(var35);
                        }
                    } else {
                        var5.close();
                    }
                }

            }
        } catch (MbException var40) {
            throw var40;
        } catch (WakeupException var41) {
        } catch (Exception var42) {
            ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "poll", "Caught exception: " + var42.toString());
            var42.printStackTrace();
            if (var42.getCause() != null) {
                System.out.println(var42.getCause().getLocalizedMessage());
                var42.getCause().printStackTrace();
            }

            this.close();
            String var6 = "3892";
            String[] var7 = new String[]{this.topicName, this.properties.getProperty("bootstrap.servers"), var42.getMessage()};
            this.containerServices.writeSystemLogError(var6, var7);
            this.containerServices.throwMbRecoverableException(var6, var7);
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaIIBConsumer", "poll", "");
        }

        return var3;
    }

    public synchronized void start() {
        ContainerServices.writeServiceTraceEntry("KafkaIIBConsumer", "start", "");
        this.stopping = false;

        try {
            KafkaClassContext var1 = KafkaClassContext.switchTo(this);
            Throwable var36 = null;

            try {
                this.initialise();
                ConsumerRecords var37 = this.kafkaConsumer.poll(0L);
                ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "start", "Received batch of " + var37.count() + " messages.");
                Iterator var4 = var37.iterator();

                while (var4.hasNext()) {
                    ConsumerRecord var5 = (ConsumerRecord) var4.next();
                    KafkaPollingResult var6 = new KafkaPollingResult(this.inputConnector, var5);
                    ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "start", "Queueing batch message " + var5.partition() + ":" + var5.offset());
                    this.receivedMessagesList.add(var6);
                }
            } catch (Throwable var30) {
                var36 = var30;
                throw var30;
            } finally {
                if (var1 != null) {
                    if (var36 != null) {
                        try {
                            var1.close();
                        } catch (Throwable var29) {
                            var36.addSuppressed(var29);
                        }
                    } else {
                        var1.close();
                    }
                }

            }
        } catch (WakeupException var32) {
        } catch (MbException var33) {
        } catch (Exception var34) {
            ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "poll", "Caught exception: " + var34.toString());
            this.close();
            String var2 = "3892";
            String[] var3 = new String[]{this.topicName, this.properties.getProperty("bootstrap.servers"), var34.getMessage()};

            try {
                this.containerServices.writeSystemLogError(var2, var3);
            } catch (MbException var28) {
            }
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaIIBConsumer", "start", "");
        }

    }

    public void stop() {
        ContainerServices.writeServiceTraceEntry("KafkaIIBConsumer", "stop", "");

        try {
            this.stopping = true;

            try {
                KafkaClassContext var1 = KafkaClassContext.switchTo(this);
                Throwable var2 = null;

                try {
                    if (this.kafkaConsumer != null) {
                        this.kafkaConsumer.wakeup();
                    }
                } catch (Throwable var20) {
                    var2 = var20;
                    throw var20;
                } finally {
                    if (var1 != null) {
                        if (var2 != null) {
                            try {
                                var1.close();
                            } catch (Throwable var19) {
                                var2.addSuppressed(var19);
                            }
                        } else {
                            var1.close();
                        }
                    }

                }
            } catch (Exception var22) {
                ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "stop", "Failed to wakeup Kafka consumer: " + var22.toString());
            }
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaIIBConsumer", "stop", "");
        }

    }

    public synchronized void close() {
        ContainerServices.writeServiceTraceEntry("KafkaIIBConsumer", "close", "");

        try {
            KafkaClassContext var1 = KafkaClassContext.switchTo(this);
            Throwable var2 = null;

            try {
                while (true) {
                    if (this.receivedMessagesList.poll() == null) {
                        if (this.kafkaConsumer != null) {
                            this.kafkaConsumer.close();
                        }
                        break;
                    }
                }
            } catch (Throwable var18) {
                var2 = var18;
                throw var18;
            } finally {
                if (var1 != null) {
                    if (var2 != null) {
                        try {
                            var1.close();
                        } catch (Throwable var17) {
                            var2.addSuppressed(var17);
                        }
                    } else {
                        var1.close();
                    }
                }

            }
        } finally {
            this.kafkaConsumer = null;
            ContainerServices.writeServiceTraceExit("KafkaIIBConsumer", "close", "");
        }

    }

    private void commitMessageOffset(int var1, long var2) throws MbException {
        ContainerServices.writeServiceTraceEntry("KafkaIIBConsumer", "commitSync", "");

        try {
            String var5;
            String[] var6;
            try {
                KafkaClassContext var4 = KafkaClassContext.switchTo(this);
                Throwable var30 = null;

                try {
                    this.kafkaConsumer.commitSync(Collections.singletonMap(new TopicPartition(this.topicName, var1), new OffsetAndMetadata(var2 + 1L)));
                } catch (Throwable var25) {
                    var30 = var25;
                    throw var25;
                } finally {
                    if (var4 != null) {
                        if (var30 != null) {
                            try {
                                var4.close();
                            } catch (Throwable var24) {
                                var30.addSuppressed(var24);
                            }
                        } else {
                            var4.close();
                        }
                    }

                }
            } catch (CommitFailedException var27) {
                this.close();
                ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "commitSync", "Caught CommitFailedException. Topic: " + this.topicName + " Partition: " + Integer.toString(var1) + " Offset: " + Long.toString(var2));
                var5 = "3896";
                var6 = new String[]{this.topicName, var1 + "::" + var2, this.clientId};
                this.containerServices.writeSystemLogError(var5, var6);
                this.containerServices.throwMbRecoverableException(var5, var6);
            } catch (Exception var28) {
                this.close();
                ContainerServices.writeServiceTraceData("KafkaIIBConsumer", "commitSync", "Caught KafkaException. Topic: " + this.topicName + " Partition: " + Integer.toString(var1) + " Offset: " + Long.toString(var2));
                var5 = "3897";
                var6 = new String[]{this.topicName, var1 + "::" + var2, this.clientId, var28.getClass().getName()};
                this.containerServices.writeSystemLogError(var5, var6);
                this.containerServices.throwMbRecoverableException(var5, var6);
            }
        } finally {
            ContainerServices.writeServiceTraceExit("KafkaIIBConsumer", "commitSync", "");
        }

    }

    private void writeRepeatingErrorToSyslog(String var1, String[] var2) {
        boolean var3 = true;
        long var4 = System.currentTimeMillis() / 1000L;
        if (this.lastMsgKey != null && this.lastMsgKey.equals(var1)) {
            if (var4 - this.lastMsgTime < (long) this.secondsUntilNextMessage) {
                var3 = false;
            }
        } else {
            this.secondsUntilNextMessage = 0;
        }

        if (var3) {
            this.lastMsgTime = var4;
            this.lastMsgKey = var1;

            try {
                this.containerServices.writeSystemLogError(var1, var2);
            } catch (MbException var7) {
            }

            switch (this.secondsUntilNextMessage) {
                case 0:
                    this.secondsUntilNextMessage = 60;
                    break;
                case 60:
                    this.secondsUntilNextMessage = 300;
                    break;
                case 300:
                    this.secondsUntilNextMessage = 1800;
                    break;
                default:
                    this.secondsUntilNextMessage = 1800;
            }
        }

    }
}

