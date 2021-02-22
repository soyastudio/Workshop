package com.albertsons.edis.kafka;

import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.*;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.StreamsConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class KafkaAdmin {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS XXX");

    private static final String BOOTSTRAP_SERVERS = "bootstrap-servers";

    private static final String CLIENT_ID = "client-id";
    private static final String KEY_SERIALIZER = "key-serializer";
    private static final String VALUE_SERIALIZER = "value-serializer";

    private static final String GROUP_ID = "group-id";
    private static final String KEY_DESERIALIZER = "key-deserializer";
    private static final String VALUE_DESERIALIZER = "value-deserializer";
    private static final String AUTO_OFFSET_RESET = "auto-offset-reset";

    private static Properties defaultProperties;

    private static final String CREATE = "create";
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";
    private static final String CONSUME = "consume";
    private static final String PRODUCE = "produce";

    private AdminClient adminClient;
    private KafkaProducer kafkaProducer;
    private KafkaConsumer kafkaConsumer;

    private Properties producerProperties;
    private Properties consumerProperties;
    private Properties adminProperties;
    private Properties streamProperties;

    static {
        defaultProperties = new Properties();
        defaultProperties.put(BOOTSTRAP_SERVERS, "localhost:9092");

        defaultProperties.put(CLIENT_ID, "test_client");
        defaultProperties.put(KEY_SERIALIZER, "org.apache.kafka.common.serialization.StringSerializer");
        defaultProperties.put(VALUE_SERIALIZER, "org.apache.kafka.common.serialization.ByteArraySerializer");

        defaultProperties.put(GROUP_ID, "test_group");
        defaultProperties.put(KEY_DESERIALIZER, "org.apache.kafka.common.serialization.StringDeserializer");
        defaultProperties.put(VALUE_DESERIALIZER, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        defaultProperties.put(AUTO_OFFSET_RESET, "earliest");
    }

    private KafkaAdmin(Properties properties) {
        StringBuilder builder = new StringBuilder();

        Properties configuration = new Properties(defaultProperties);
        configuration.putAll(properties);

        producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getProperty(BOOTSTRAP_SERVERS));
        producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, configuration.getProperty(CLIENT_ID));
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, configuration.getProperty(KEY_SERIALIZER));
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, configuration.getProperty(VALUE_SERIALIZER));
        //props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class.getName());


        consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getProperty(BOOTSTRAP_SERVERS));
        consumerProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, configuration.getProperty(CLIENT_ID));
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, configuration.getProperty(GROUP_ID));
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, configuration.getProperty(KEY_DESERIALIZER));
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, configuration.getProperty(VALUE_DESERIALIZER));
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, configuration.getProperty(AUTO_OFFSET_RESET));

        this.adminProperties = new Properties();
        adminProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getProperty(BOOTSTRAP_SERVERS));

        this.streamProperties = new Properties();
        streamProperties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, configuration.getProperty(BOOTSTRAP_SERVERS));
    }

    public KafkaAdmin(AdminClient adminClient, KafkaProducer kafkaProducer, KafkaConsumer kafkaConsumer) {
        this.adminClient = adminClient;
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
    }

    public Map<MetricName, ? extends Metric> metrics() {
        return adminClient.metrics();
    }

    // ==================== admin:
    public ClusterModel cluster() {
        DescribeClusterResult result = adminClient.describeCluster();
        return ClusterModel.fromDescribeClusterResult(result);
    }

    public Collection<ConsumerGroupListing> consumerGroups() {
        KafkaFuture<Collection<ConsumerGroupListing>> future = adminClient.listConsumerGroups().all();
        while (future.isDone()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            return future.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new KafkaAdminException(e);
        }
    }

    public Map<TopicPartition, OffsetAndMetadata> listConsumerGroupOffsetsIfAuthorized(String groupId) {
        KafkaFuture<Map<TopicPartition, OffsetAndMetadata>> future = adminClient.listConsumerGroupOffsets(groupId).partitionsToOffsetAndMetadata();
        while (future.isDone()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            return future.get();

        } catch (InterruptedException | ExecutionException e) {
            throw new KafkaAdminException(e);
        }
    }

    public Set<String> topicNames() {
        Future<Set<String>> future = adminClient.listTopics().names();
        while (!future.isDone()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
        }

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<PartitionInfo> topic(String topic) {
        return kafkaConsumer.partitionsFor(topic);
    }

    public void createTopic(Set<NewTopic> newTopics) {
        adminClient.createTopics(newTopics);
    }

    public void deleteTopic(String topicName) {
        Set<String> set = new HashSet<>();
        set.add(topicName);
        adminClient.deleteTopics(set);
    }

    // ================= Producer:
    public RecordModel publish(String topic, String message) {
        ProducerRecord<String, byte[]> record = RecordModel.builder(topic).generateKey().message(message).create();
        Future<RecordMetadata> future = kafkaProducer.send(record);
        while (!future.isDone()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            RecordMetadata metadata = future.get();
            return RecordModel.fromProducerRecord(record, metadata);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Future<RecordMetadata> send(String topic, String key, byte[] value) {
        ProducerRecord<String, byte[]> producerRecord = new ProducerRecord(topic, value);
        return kafkaProducer.send(producerRecord);
    }

    // ================= Consumer:
    public List<ConsumerRecord<String, byte[]>> getLatestRecords(String topic, int count) {
        List<PartitionInfo> partitionInwriteret = kafkaConsumer.partitionsFor(topic);
        Collection<TopicPartition> partitions = partitionInwriteret.stream()
                .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                        partitionInfo.partition()))
                .collect(Collectors.toList());
        kafkaConsumer.assign(partitions);

        Map<TopicPartition, Long> latestOffsets = kafkaConsumer.endOffsets(partitions);
        for (TopicPartition partition : partitions) {
            Long latestOffset = Math.max(0, latestOffsets.get(partition) - 1);
            kafkaConsumer.seek(partition, Math.max(0, latestOffset - count));
        }

        int totalCount = count * partitions.size();
        final Map<TopicPartition, List<ConsumerRecord<String, byte[]>>> rawRecords
                = partitions.stream().collect(Collectors.toMap(p -> p, p -> new ArrayList<>(count)));

        boolean moreRecords = true;
        while (rawRecords.size() < totalCount && moreRecords) {
            ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(200));

            moreRecords = false;
            for (TopicPartition partition : polled.partitions()) {
                List<ConsumerRecord<String, byte[]>> records = polled.records(partition);
                if (!records.isEmpty()) {
                    rawRecords.get(partition).addAll(records);
                    moreRecords = records.get(records.size() - 1).offset() < latestOffsets.get(partition) - 1;
                }
            }
        }

        return rawRecords
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(rec -> new ConsumerRecord<String, byte[]>(rec.topic(),
                        rec.partition(),
                        rec.offset(),
                        rec.timestamp(),
                        rec.timestampType(),
                        0L,
                        rec.serializedKeySize(),
                        rec.serializedValueSize(),
                        rec.key(),
                        rec.value(),
                        rec.headers(),
                        rec.leaderEpoch()))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {

        File home = new File(Paths.get("").toAbsolutePath().toString());
        Properties properties = new Properties();
        File configFile = new File(home, "kafka-config.properties");
        if (configFile.exists()) {
            try {
                properties.load(new FileInputStream(configFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        KafkaAdmin kafkaAdmin = new KafkaAdmin(properties);

        // Command line definition:
        Options options = new Options();
        options.addOption(Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Help ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("a")
                .longOpt("action")
                .hasArg(true)
                .desc("Create business object test workspace.")
                .required(false)
                .build());

        options.addOption(Option.builder("b")
                .longOpt("businessObject")
                .hasArg(true)
                .desc("Business object.")
                .required(false)
                .build());

        options.addOption(Option.builder("c")
                .longOpt("outboundTopic")
                .hasArg(true)
                .desc("Consume from Outbound Topic")
                .required(false)
                .build());

        options.addOption(Option.builder("f")
                .longOpt("logFile")
                .hasArg(true)
                .desc("File for logging the task result.")
                .required(false)
                .build());

        options.addOption(Option.builder("h")
                .longOpt("header")
                .hasArg(true)
                .desc("Kafka message header")
                .required(false)
                .build());

        options.addOption(Option.builder("m")
                .longOpt("msg")
                .hasArg(true)
                .desc("Message for Kafka producer")
                .required(false)
                .build());

        options.addOption(Option.builder("p")
                .longOpt("inboundTopic")
                .hasArg(true)
                .desc("Publish to Inbound Topic, m required.")
                .required(false)
                .build());

        options.addOption(Option.builder("x")
                .longOpt("cleanHistory")
                .hasArg(false)
                .desc("Clean log history during process.")
                .required(false)
                .build());

        // Parse command line and dispatch to method:
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        if (cmd.hasOption("b")) {
            processBusinessObjectTask(cmd, kafkaAdmin, home);

        } else {
            processKafkaAdminTask(cmd, kafkaAdmin);

        }
    }

    private static void processBusinessObjectTask(CommandLine cmd, KafkaAdmin kafkaAdmin, File home) {

        File dir = new File(home, cmd.getOptionValue("b"));
        File bod = new File(dir, "bod.properties");
        String action = cmd.hasOption("a") ? cmd.getOptionValue("a") : null;
        int exit = 0;

        File logFile = new File(dir, "test.log");
        boolean cleanLogHistory = cmd.hasOption("x");

        StringBuilder logger = new StringBuilder();
        if (action == null) {
            action = PRODUCE;
        }

        logger.append("==================== Execute task '").append(action).append("' on ").append(new Date()).append(" ====================").append("\n");

        try {
            if (CREATE.equalsIgnoreCase(action)) {
                createWorkspace(cmd, dir);

            } else if (UPDATE.equalsIgnoreCase(action)) {
                updateWorkspace(cmd, dir);

            } else if (DELETE.equalsIgnoreCase(action)) {
                deleteWorkspace(cmd, dir);

            } else if (CONSUME.equalsIgnoreCase(action)) {
                consume(cmd, dir, kafkaAdmin, logger);

            } else if (PRODUCE.equalsIgnoreCase(action)) {
                produce(cmd, dir, kafkaAdmin, logger);

            } else {
                System.out.println("Unknown action: " + action);

            }
        } catch (Exception e) {
            logger.append("ERROR: " + e.getMessage()).append("\n");
            logger.append("Root cause: " + e.getCause().getClass().getName()).append("\n");

            System.out.println("Error: '" + e.getMessage() + "': Root cause: " + e.getCause().getClass().getName());
            exit = 1;

        } finally {
            logger.append("\n").append("\n").append("\n");
            if (logFile.exists()) {
                try {
                    if (cmd.hasOption("x")) {
                        Files.write(Paths.get(logFile.toURI()), logger.toString().getBytes(StandardCharsets.UTF_8));
                    } else {
                        Files.write(Paths.get(logFile.toURI()), logger.toString().getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            System.exit(exit);
        }
    }

    private static void processKafkaAdminTask(CommandLine cmd, KafkaAdmin kafkaAdmin) {

    }

    // Business Object Tasks:
    private static void createWorkspace(CommandLine cmd, File dir) throws IOException {
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File configFile = new File(dir, "bod.properties");
        if (!configFile.exists()) {
            configFile.createNewFile();
        }

        File logFile = new File(dir, "test.log");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        String p = cmd.hasOption("p") ? cmd.getOptionValue("p") : "";
        String c = cmd.hasOption("c") ? cmd.getOptionValue("c") : "";
        String m = cmd.hasOption("m") ? cmd.getOptionValue("m") : "";

        FileWriter writer = new FileWriter(configFile);
        writer.write("c=" + c + "\n");
        writer.write("p=" + p + "\n");
        writer.write("m=" + m + "\n");

        writer.flush();
        writer.close();

    }

    private static void updateWorkspace(CommandLine cmd, File dir) throws IOException {
        if (!dir.exists()) {
            System.out.println("Directory '" + dir.getName() + "' does not exist.");
            System.exit(1);
        }

        File configFile = new File(dir, "bod.properties");
        if (!configFile.exists()) {
            System.out.println("Business Object '" + dir.getName() + "' does not exist.");
            System.exit(1);
        }

        Properties properties = new Properties();
        properties.load(new FileInputStream(configFile));

        String p = cmd.hasOption("p") ? cmd.getOptionValue("p") : properties.getProperty("p");
        String c = cmd.hasOption("c") ? cmd.getOptionValue("c") : properties.getProperty("c");
        String m = cmd.hasOption("m") ? cmd.getOptionValue("m") : properties.getProperty("m");

        FileWriter writer = new FileWriter(configFile);
        writer.write("c=" + c + "\n");
        writer.write("p=" + p + "\n");
        writer.write("m=" + m + "\n");

        writer.flush();
        writer.close();
    }

    private static void deleteWorkspace(CommandLine cmd, File dir) throws IOException {
        FileUtils.forceDelete(dir);
    }

    private static void consume(CommandLine cmd, File dir, KafkaAdmin kafkaAdmin, StringBuilder logger) throws IOException {
        logger.append("Kafka Consumer:").append("\n");

        long timestamp = System.currentTimeMillis() - 500l;

        Properties bod = new Properties();
        bod.load(new FileInputStream(new File(dir, "bod.properties")));
        String topic = cmd.hasOption("c") ? cmd.getOptionValue("c") : bod.getProperty("c");

        KafkaConsumer<String, byte[]> kafkaConsumer = new KafkaConsumer(kafkaAdmin.consumerProperties);
        int count = 10;

        List<PartitionInfo> partitionInwriteret = kafkaConsumer.partitionsFor(topic);
        Collection<TopicPartition> partitions = partitionInwriteret.stream()
                .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                        partitionInfo.partition()))
                .collect(Collectors.toList());
        kafkaConsumer.assign(partitions);

        Map<TopicPartition, Long> latestOffsets = kafkaConsumer.endOffsets(partitions);
        for (TopicPartition partition : partitions) {
            Long latestOffset = Math.max(0, latestOffsets.get(partition) - 1);
            kafkaConsumer.seek(partition, Math.max(0, latestOffset - count));
        }

        int totalCount = count * partitions.size();
        final Map<TopicPartition, List<ConsumerRecord<String, byte[]>>> rawRecords
                = partitions.stream().collect(Collectors.toMap(p -> p, p -> new ArrayList<>(count)));

        boolean moreRecords = true;
        while (rawRecords.size() < totalCount && moreRecords) {
            ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(5000));

            moreRecords = false;
            for (TopicPartition partition : polled.partitions()) {
                List<ConsumerRecord<String, byte[]>> records = polled.records(partition);
                if (!records.isEmpty()) {
                    rawRecords.get(partition).addAll(records);
                    moreRecords = records.get(records.size() - 1).offset() < latestOffsets.get(partition) - 1;
                }
            }
        }

        List<ConsumerRecord<String, byte[]>> latestRecords = rawRecords
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(rec -> new ConsumerRecord<String, byte[]>(rec.topic(),
                        rec.partition(),
                        rec.offset(),
                        rec.timestamp(),
                        rec.timestampType(),
                        0L,
                        rec.serializedKeySize(),
                        rec.serializedValueSize(),
                        rec.key(),
                        rec.value(),
                        rec.headers(),
                        rec.leaderEpoch()))
                .collect(Collectors.toList());

        if (latestRecords.size() > 0) {
            latestRecords.forEach(rc -> {
                //if(rc.timestamp() > timestamp) {
                logger.append("Timestamp: ").append(DATE_FORMAT.format(new Date(rc.timestamp()))).append("\n");
                logger.append("Topic: ").append(rc.topic()).append("\n");
                logger.append("Partition: ").append(rc.partition()).append("\n");
                logger.append("Offset: ").append(rc.offset()).append("\n");
                logger.append("Headers: ").append(toString(rc.headers())).append("\n");
                logger.append("Key: ").append(rc.key()).append("\n");
                logger.append("Message: ").append(new String(rc.value())).append("\n");

                logger.append("\n");
                //}
            });
        }

    }

    private static void produce(CommandLine cmd, File dir, KafkaAdmin kafkaAdmin, StringBuilder logger) throws IOException {
        logger.append("Kafka Producer:").append("\n");

        Properties bod = new Properties();
        bod.load(new FileInputStream(new File(dir, "bod.properties")));

        String ibTopic = cmd.hasOption("p") ? cmd.getOptionValue("p") : bod.getProperty("p");
        String obTopic = cmd.hasOption("c") ? cmd.getOptionValue("c") : bod.getProperty("c");
        String headers = cmd.hasOption("h") ? cmd.getOptionValue("h") : null;
        String message = cmd.hasOption("m") ? cmd.getOptionValue("m") : bod.getProperty("m");

        if (ibTopic != null) {
            KafkaProducer kafkaProducer = new KafkaProducer(kafkaAdmin.producerProperties);
            RecordModel.Builder builder = RecordModel.builder(ibTopic);
            if (headers != null) {
                String[] array = headers.split(",");
                for (int i = 0; i < array.length; i++) {
                    String token = array[i];
                    if (token.contains("=")) {
                        String[] kv = token.split("=");
                        if (kv.length == 2) {
                            builder.header(kv[0].trim(), kv[1].trim());
                        } else {

                        }
                    }
                }
            }

            ProducerRecord<String, byte[]> record = builder.generateKey().message(message).create();

            Future<RecordMetadata> future = kafkaProducer.send(record);
            while (!future.isDone()) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                RecordMetadata metadata = future.get();
                RecordModel.fromProducerRecord(record, metadata);

                logger.append("Timestamp: ").append(DATE_FORMAT.format(new Date(metadata.timestamp()))).append("\n");
                logger.append("Topic: ").append(metadata.topic()).append("\n");
                logger.append("Partition: ").append(metadata.partition()).append("\n");
                logger.append("Offset: ").append(metadata.offset()).append("\n");
                logger.append("Headers: ").append(toString(record.headers())).append("\n");
                logger.append("Key: ").append(record.key()).append("\n");
                logger.append("Message: ").append(new String(record.value())).append("\n");

                logger.append("\n");


            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            if (obTopic != null) {
                consume(cmd, dir, kafkaAdmin, logger);
            }
        }
    }

    // Global methods:
    private static void kafkaPublish() {

    }

    // utility methods:
    private static String toString(Headers headers) {
        StringBuilder builder = new StringBuilder();
        headers.forEach(e -> {
            builder.append(e.key()).append("=").append(new String(e.value())).append(",");
        });

        String token = builder.toString();
        if (token.endsWith(",")) {
            token = token.substring(0, token.length() - 1);
        }

        return token;
    }
}
