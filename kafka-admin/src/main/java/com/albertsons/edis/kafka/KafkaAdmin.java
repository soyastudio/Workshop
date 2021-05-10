package com.albertsons.edis.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import groovy.lang.GroovyShell;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
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
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class KafkaAdmin {
    private static Logger logger = LoggerFactory.getLogger("KafkaAdmin");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static Properties defaultProperties;

    private static final String CREATE = "create";
    private static final String UPDATE = "update";
    private static final String FILE = "file";
    private static final String DELETE = "delete";

    private static final String HELP = "help";
    private static final String METRICS = "metrics";
    private static final String TOPICS = "topics";
    private static final String TOPIC = "topic";

    private static final String PRODUCE = "produce";
    private static final String CONSUME = "consume";
    private static final String LATEST = "latest";
    private static final String PUB_AND_SUB = "pubAndSub";

    private static final String APPLICATION = "application";
    private static final String STREAM = "stream";
    private static final String GROOVY = "groovy";

    private static final String UNDEPLOY = "undeploy";

    private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private Properties producerProperties;
    private Properties consumerProperties;
    private Properties adminProperties;
    private Properties streamProperties;

    static {
        defaultProperties = new Properties();
        // common
        defaultProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        defaultProperties.setProperty(CommonClientConfigs.CLIENT_ID_CONFIG, "test_client");
        defaultProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, CommonClientConfigs.DEFAULT_SECURITY_PROTOCOL);

        // producer
        defaultProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        defaultProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");

        // consumer
        defaultProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "test_group");
        defaultProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        defaultProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        defaultProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    }

    private KafkaAdmin() {
        // Producer properties:
        this.producerProperties = new Properties();
        producerProperties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, getProperty(ProducerConfig.CLIENT_ID_CONFIG));
        producerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, getProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, getProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));

        producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        producerProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));

        if ("SSL".equalsIgnoreCase(producerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            producerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            producerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            producerProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            producerProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(producerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            producerProperties.setProperty(SaslConfigs.SASL_MECHANISM, getProperty(SaslConfigs.SASL_MECHANISM));
            producerProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        }

        // Consumer properties:
        this.consumerProperties = new Properties();

        consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, getProperty(ConsumerConfig.CLIENT_ID_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, getProperty(ConsumerConfig.GROUP_ID_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, getProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, getProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
        consumerProperties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");

        consumerProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
        if ("SSL".equalsIgnoreCase(consumerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            consumerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            consumerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            consumerProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            consumerProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(consumerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            consumerProperties.setProperty(SaslConfigs.SASL_MECHANISM, getProperty(SaslConfigs.SASL_MECHANISM));
            consumerProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        }

        // Admin properties:
        this.adminProperties = new Properties();
        adminProperties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG));
        adminProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));

        if ("SSL".equalsIgnoreCase(adminProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            adminProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            adminProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            adminProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            adminProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(adminProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            adminProperties.setProperty(SaslConfigs.SASL_MECHANISM, getProperty(SaslConfigs.SASL_MECHANISM));
            adminProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        }

        // Stream properties:
        this.streamProperties = new Properties();
        streamProperties.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, getProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG));
        streamProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));

        if ("SSL".equalsIgnoreCase(streamProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            streamProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            streamProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            streamProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            streamProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(streamProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            streamProperties.setProperty(SaslConfigs.SASL_MECHANISM, getProperty(SaslConfigs.SASL_MECHANISM));
            streamProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        }
    }

    private KafkaProducer createKafkaProducer() {
        return new KafkaProducer(producerProperties);
    }

    private KafkaConsumer createKafkaConsumer() {
        return new KafkaConsumer(consumerProperties);
    }

    private AdminClient createAdminClient() {
        return AdminClient.create(adminProperties);
    }

    private Properties createStreamConfiguration(String applicationId) {

        Properties streamsConfiguration = new Properties(streamProperties);
        streamsConfiguration.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, streamProperties.getProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG));
        streamsConfiguration.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        streamsConfiguration.setProperty(StreamsConfig.CLIENT_ID_CONFIG, applicationId + "-client");
        streamsConfiguration.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsConfiguration.setProperty(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "" + 10 * 1000);
        streamsConfiguration.setProperty(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "" + 0);

        return streamsConfiguration;
    }

    private static String env = "LOCAL";
    private static File home;
    private static File workspace;
    private static File streams;
    private static File scripts;

    private static Properties configuration;

    public static void main(String[] args) {

        File bin = new File(Paths.get("").toAbsolutePath().toString());
        home = bin.getParentFile();
        System.setProperty("kafkaya.home", home.getAbsolutePath());

        workspace = new File(home, "workspace");
        if (!workspace.exists()) {
            workspace.mkdirs();
        }

        streams = new File(home, "streams");
        if (!streams.exists()) {
            streams.mkdirs();
        }

        scripts = new File(home, "scripts");
        if (!scripts.exists()) {
            scripts.mkdirs();
        }

        configuration = new Properties(defaultProperties);
        File configFile = new File(bin, "kafka-config.properties");
        if (configFile.exists()) {
            try {
                configuration.load(new FileInputStream(configFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Command line definition:
        Options options = new Options();
        options.addOption(Option.builder("h")
                .longOpt("help")
                .hasArg(true)
                .desc("Help ([OPTIONAL])")
                .required(false)
                .build());

        options.addOption(Option.builder("a")
                .longOpt("action")
                .hasArg(true)
                .desc("Task to execute.")
                .required(false)
                .build());

        options.addOption(Option.builder("b")
                .longOpt("businessObject")
                .hasArg(true)
                .desc("Business object.")
                .required(false)
                .build());

        options.addOption(Option.builder("c")
                .longOpt("consumerTopic")
                .hasArg(true)
                .desc("Consumer Topic")
                .required(false)
                .build());

        options.addOption(Option.builder("d")
                .longOpt("dir")
                .hasArg(true)
                .desc("Directory")
                .required(false)
                .build());

        options.addOption(Option.builder("e")
                .longOpt("action")
                .hasArg(true)
                .desc("Environment, default 'local', case insensitive.")
                .required(false)
                .build());

        options.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("File related to the action specified.")
                .required(false)
                .build());

        options.addOption(Option.builder("H")
                .longOpt("header")
                .hasArg(true)
                .desc("Kafka message header")
                .required(false)
                .build());

        options.addOption(Option.builder("k")
                .longOpt("key")
                .hasArg(true)
                .desc("Kafka message key")
                .required(false)
                .build());

        options.addOption(Option.builder("m")
                .longOpt("msg")
                .hasArg(true)
                .desc("Message for Kafka producer")
                .required(false)
                .build());

        options.addOption(Option.builder("n")
                .longOpt("count")
                .hasArg(true)
                .desc("Number of message to print out.")
                .required(false)
                .build());

        options.addOption(Option.builder("O")
                .longOpt("Offset")
                .hasArg(true)
                .desc("Offset.")
                .required(false)
                .build());

        options.addOption(Option.builder("P")
                .longOpt("partition")
                .hasArg(true)
                .desc("Partition.")
                .required(false)
                .build());

        options.addOption(Option.builder("p")
                .longOpt("producerTopic")
                .hasArg(true)
                .desc("Producer Topic.")
                .required(false)
                .build());

        options.addOption(Option.builder("Q")
                .longOpt("Query")
                .hasArg(false)
                .desc("Find All Topics.")
                .required(false)
                .build());

        options.addOption(Option.builder("q")
                .longOpt("query")
                .hasArg(true)
                .desc("Query.")
                .required(false)
                .build());

        options.addOption(Option.builder("s")
                .longOpt("streamClass")
                .hasArg(true)
                .desc("Stream class to execute.")
                .required(false)
                .build());

        options.addOption(Option.builder("t")
                .longOpt("taskMethod")
                .hasArg(true)
                .desc("Task method of Kafka streams class.")
                .required(false)
                .build());

        options.addOption(Option.builder("T")
                .longOpt("timeout")
                .hasArg(true)
                .desc("Timeout ")
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
            log(e.getMessage());
            System.exit(1);
        }

        // Environment:
        if (cmd.hasOption("e")) {
            env = cmd.getOptionValue("e").toUpperCase();
        }
        System.setProperty("kafka." + CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, getProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG));
        System.setProperty("kafka." + CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
        System.setProperty("kafka." + CommonClientConfigs.CLIENT_ID_CONFIG, getProperty(CommonClientConfigs.CLIENT_ID_CONFIG));

        System.setProperty("kafka." + ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, getProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
        System.setProperty("kafka." + ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, getProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));

        System.setProperty("kafka." + ConsumerConfig.GROUP_ID_CONFIG, getProperty(ConsumerConfig.GROUP_ID_CONFIG));
        System.setProperty("kafka." + ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, getProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        System.setProperty("kafka." + ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, getProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
        System.setProperty("kafka." + ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, getProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));

        if ("SSL".equalsIgnoreCase(getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            System.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            System.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            System.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            System.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            System.setProperty(SaslConfigs.SASL_MECHANISM, getProperty(SaslConfigs.SASL_MECHANISM));
            System.setProperty(SaslConfigs.SASL_JAAS_CONFIG, getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        }

        // Context:
        executeCommandLine(cmd, new KafkaAdmin());
    }

    private static String getProperty(String propName) {
        if (System.getProperty("kafka." + propName) != null) {
            return System.getProperty("kafka." + propName);
        }

        String key = env + "." + propName;
        if (configuration.containsKey(key)) {
            return configuration.getProperty(key);
        }

        return defaultProperties.getProperty(propName);
    }

    // Global methods:
    private static void log(String msg) {
        System.out.println(msg);
    }

    public static void help(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        if (cmd.hasOption("a")) {


        } else {

        }

    }

    public static void metrics(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        List<Metric> metricList = new ArrayList<>(kafkaAdmin.createAdminClient().metrics().values());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        log(gson.toJson(metricList));
    }

    public static void topics(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        String q = null;
        try {
            q = cmd.getOptionValue("q");

        } catch (Exception e) {

        }

        String query = q;

        AdminClient adminClient = kafkaAdmin.createAdminClient();
        Future<Set<String>> future = adminClient.listTopics().names();
        while (!future.isDone()) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
            }
        }

        try {
            List<String> topics = new ArrayList<>(future.get());
            Collections.sort(topics);
            topics.forEach(e -> {
                if (query == null) {
                    log(e);

                } else if (e.startsWith(query)) {
                    log(e);

                }
            });

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void topic(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        if (!cmd.hasOption("c")) {
            throw new IllegalArgumentException("Please specify topic name using command line argument '-c'");
        }

        String topicName = cmd.getOptionValue("c");
        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaAdmin.createKafkaConsumer();

        List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
        Collection<PartitionStatus> partitionStatuses = partitionInfoSet.stream()
                .map(partitionInfo -> new PartitionStatus(partitionInfo))
                .collect(Collectors.toList());

        Collection<TopicPartition> partitions = partitionStatuses.stream()
                .map(e -> e.topicPartition)
                .collect(Collectors.toList());
        kafkaConsumer.assign(partitions);

        Map<TopicPartition, Long> beginOffsets = kafkaConsumer.beginningOffsets(partitions);
        Map<TopicPartition, Long> endOffsets = kafkaConsumer.endOffsets(partitions);

        partitionStatuses.forEach(e -> {
            if (beginOffsets.containsKey(e.topicPartition)) {
                e.offset.begin = beginOffsets.get(e.topicPartition);
            }

            if (endOffsets.containsKey(e.topicPartition)) {
                e.offset.end = endOffsets.get(e.topicPartition);
            }

        });

        log(GSON.toJson(partitionStatuses));
    }

    public static void partitions(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        if (!cmd.hasOption("c")) {
            throw new IllegalArgumentException("Please specify topic name using command line argument '-c'");
        }

        String topicName = cmd.getOptionValue("c");
        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaAdmin.createKafkaConsumer();
        List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);

        log(GSON.toJson(partitionInfoSet));

    }

    public static void offsets(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        if (!cmd.hasOption("c")) {
            throw new IllegalArgumentException("Please specify topic name using command line argument '-c'");
        }

        String topicName = cmd.getOptionValue("c");
        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaAdmin.createKafkaConsumer();
        List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
        Collection<TopicPartition> partitions = partitionInfoSet.stream()
                .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                        partitionInfo.partition()))
                .collect(Collectors.toList());
        kafkaConsumer.assign(partitions);

        Map<TopicPartition, Long> beginOffsets = kafkaConsumer.beginningOffsets(partitions);
        Map<TopicPartition, Long> endOffsets = kafkaConsumer.endOffsets(partitions);

        endOffsets.entrySet().forEach(e -> {
            log("Partition_" + e.getKey().partition() + ": " + beginOffsets.get(e.getKey()) + " - " + e.getValue());

        });
    }

    public static void pubAndSub(CommandLine cmd, KafkaAdmin kafkaAdmin) throws Exception {
        long timestamp = System.currentTimeMillis();

        log("Producing message...");
        produce(cmd, kafkaAdmin);
        log("Message produced.");

        log("Consuming message...");

        Thread.sleep(300l);
        String topicName = null;

        if (cmd.hasOption("b")) {
            File dir = new File(workspace, cmd.getOptionValue("b"));
            File bod = new File(dir, "bod.properties");
            if (!bod.exists()) {
                log("File '" + bod + "' does not exit.");
            }

            Properties properties = new Properties();
            properties.load(new FileInputStream(bod));

            topicName = properties.containsKey(env + ".c") ? properties.getProperty(env + ".c") : properties.getProperty("c");
        }

        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        if (topicName == null) {
            log("ERROR: Topic is not set. Please set topic name using parameter 'c'.");
            System.exit(1);
        }

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaAdmin.createKafkaConsumer();
        List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
        Collection<TopicPartition> partitions = partitionInfoSet.stream()
                .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                        partitionInfo.partition()))
                .collect(Collectors.toList());

        Map<TopicPartition, Long> latestOffsets = kafkaConsumer.endOffsets(partitions);

        while (true) {
            if (System.currentTimeMillis() - timestamp > 60000) {
                throw new TimeoutException("Cannot get message from topic: " + topicName);
            }

            for (TopicPartition partition : partitions) {
                List<TopicPartition> assignments = new ArrayList<>();
                assignments.add(partition);
                kafkaConsumer.assign(assignments);

                Long latestOffset = Math.max(0, latestOffsets.get(partition) - 1);
                kafkaConsumer.seek(partition, Math.max(0, latestOffset));
                ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(300));

                polled.forEach(rc -> {
                    if (rc.timestamp() > timestamp) {
                        log("Timestamp: " + DATE_FORMAT.format(new Date(rc.timestamp())));
                        log("Topic: " + rc.topic());
                        log("Partition: " + rc.partition());
                        log("Offset: " + rc.offset());
                        log("Headers: " + toString(rc.headers()));
                        log("Key: " + rc.key());
                        log("Message: ");
                        log(prettyPrint(new String(rc.value())));

                        log("Message consumed.");

                        return;
                    }
                });
            }
        }
    }

    public static void pub(CommandLine cmd, KafkaAdmin kafkaAdmin) throws Exception {
        produce(cmd, kafkaAdmin);
    }

    public static void publish(CommandLine cmd, KafkaAdmin kafkaAdmin) throws Exception {
        produce(cmd, kafkaAdmin);
    }

    public static RecordMetadata produce(CommandLine cmd, KafkaAdmin kafkaAdmin) throws Exception {
        String topicName = null;
        String fileName = null;

        if (cmd.hasOption("b")) {
            Properties properties = getBodConfig(cmd);
            topicName = getBodProperty("p", properties);
            fileName = getBodProperty("f", properties);
            if (fileName == null) {
                fileName = "input.json";
            }
            fileName = "workspace/" + cmd.getOptionValue("b") + "/" + fileName;
        }

        if (cmd.hasOption("p")) {
            topicName = cmd.getOptionValue("p");
        }

        if (topicName == null) {
            log("ERROR: Topic is not set. Please set topic name using parameter 'p'.");
            System.exit(1);
        }

        if (cmd.hasOption("f")) {
            fileName = cmd.getOptionValue("f");
        }

        if (fileName == null) {
            log("ERROR: Message is not set. Please set file name using parameter 'f'.");
            System.exit(1);
        }

        File file = new File(home, fileName);
        if (!file.exists()) {
            log("File does not exist: " + file.getAbsolutePath());
            System.exit(1);
        }

        byte[] msg = FileUtils.readFileToByteArray(file);
        String hs = cmd.hasOption("H") ? cmd.getOptionValue("H") : null;
        String key = cmd.hasOption("k") ? cmd.getOptionValue("k") : UUID.randomUUID().toString();

        return produce(kafkaAdmin, topicName, file, hs, key);

    }

    public static List<RecordInfo> list(CommandLine cmd, KafkaAdmin kafkaAdmin) throws Exception {
        Set<RecordInfo> results = new HashSet<>();
        String topicName = null;

        if (cmd.hasOption("b")) {
            File dir = new File(workspace, cmd.getOptionValue("b"));
            File bod = new File(dir, "bod.properties");
            if (!bod.exists()) {
                log("File '" + bod + "' does not exit.");
            }

            Properties properties = new Properties();
            properties.load(new FileInputStream(bod));

            topicName = properties.containsKey(env + ".c") ? properties.getProperty(env + ".c") : properties.getProperty("c");
        }

        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        if (topicName == null) {
            log("ERROR: Topic is not set. Please set topic name using parameter 'c'.");
            System.exit(1);
        }

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaAdmin.createKafkaConsumer();
        Collection<TopicPartition> partitions = null;
        if (cmd.hasOption("P")) {
            partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topicName, Integer.parseInt(cmd.getOptionValue("P"))));

        } else {
            List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
            partitions = partitionInfoSet.stream()
                    .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                            partitionInfo.partition()))
                    .collect(Collectors.toList());

        }

        long timeout = 500L;
        if(cmd.hasOption("T")) {
            timeout = Long.parseLong(cmd.getOptionValue("T"));
        }

        int count = 1;
        if (cmd.hasOption("n")) {
            count = Math.max(1, Integer.parseInt(cmd.getOptionValue("n")));
        }

        kafkaConsumer.assign(partitions);
        kafkaConsumer.seekToBeginning(partitions);

        for (int i = 0; i < count; i++) {
            for (TopicPartition partition : partitions) {
                List<TopicPartition> assignments = new ArrayList<>();
                assignments.add(partition);
                kafkaConsumer.assign(assignments);
                kafkaConsumer.seekToBeginning(assignments);
                ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(timeout));

                polled.forEach(rc -> {
                    results.add(new RecordInfo(rc));
                });
            }
        }

        kafkaConsumer.close();

        List<RecordInfo> list = new ArrayList<>(results);
        Collections.sort(list);

        log("Total number: " + list.size());
        log("");

        log("| " + cellFormat("Timestamp", 24) + " | "
                + cellFormat("Partition", 9) + " | "
                + cellFormat("Offset", 6) + " | "
                + cellFormat("Key", 32) + " | "
                + cellFormat("Size", 8) + " | "
                + cellFormat("Headers", 40) + " |");
        log("  " + "------------------------"
                + "   " + "---------"
                + "   " + "------"
                + "   " + "--------------------------------"
                + "   " + "--------"
                + "   " + "----------------------------------------");

        list.forEach(e -> {
            log("| " + cellFormat(e.timestamp, 24) + " | " + cellFormat("" + e.partition, 9) + " | "
                    + cellFormat("" + e.offset, 6) + " | " + cellFormat(e.key, 32) + " | "
                    + cellFormat("" + e.size, 8) + " | "
                    + cellFormat(e.headers, 40) + " |");
        });

        return list;

    }

    public static ConsumerRecord<String, byte[]> latest(CommandLine cmd, KafkaAdmin kafkaAdmin) throws Exception {
        String topicName = null;

        if (cmd.hasOption("b")) {
            File dir = new File(workspace, cmd.getOptionValue("b"));
            File bod = new File(dir, "bod.properties");
            if (!bod.exists()) {
                log("File '" + bod + "' does not exit.");
            }

            Properties properties = new Properties();
            properties.load(new FileInputStream(bod));

            topicName = properties.containsKey(env + ".c") ? properties.getProperty(env + ".c") : properties.getProperty("c");
        }

        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        if (topicName == null) {
            log("ERROR: Topic is not set. Please set topic name using parameter 'c'.");
            System.exit(1);
        }

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaAdmin.createKafkaConsumer();
        List<String> topics = new ArrayList<>();
        topics.add(topicName);

        Collection<TopicPartition> partitions = null;
        if (cmd.hasOption("P")) {
            partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topicName, Integer.parseInt(cmd.getOptionValue("P"))));

        } else {
            List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
            partitions = partitionInfoSet.stream()
                    .map(partitionInfo -> new TopicPartition(partitionInfo.topic(),
                            partitionInfo.partition()))
                    .collect(Collectors.toList());

        }

        ConsumerRecord<String, byte[]> rc = latest(kafkaConsumer, topicName, partitions);
        if (rc != null) {
            log("Timestamp: " + DATE_FORMAT.format(new Date(rc.timestamp())));
            log("Topic: " + rc.topic());
            log("Partition: " + rc.partition());
            log("Offset: " + rc.offset());
            log("Key: " + rc.key());
            log("Headers: " + toString(rc.headers()));
            log("Message: ");
            log(prettyPrint(new String(rc.value())));

        } else {
            log("Cannot get message from topic: " + topicName);
        }

        return rc;
    }



    public static void consume(CommandLine cmd, KafkaAdmin kafkaAdmin) throws Exception {
        KafkaConsumer kafkaConsumer = kafkaAdmin.createKafkaConsumer();
        String topicName = null;

        if (cmd.hasOption("b")) {
            File dir = new File(workspace, cmd.getOptionValue("b"));
            File bod = new File(dir, "bod.properties");
            if (!bod.exists()) {
                log("File '" + bod + "' does not exit.");
            }

            Properties properties = new Properties();
            properties.load(new FileInputStream(bod));

            topicName = properties.containsKey(env + ".c") ? properties.getProperty(env + ".c") : properties.getProperty("c");
        }

        if (cmd.hasOption("c")) {
            topicName = cmd.getOptionValue("c");
        }

        if (topicName == null) {
            log("ERROR: Topic is not set. Please set topic name using parameter 'c'.");
            System.exit(1);
        }

        long timeout = 1000;
        if(cmd.hasOption("T")) {
            timeout = Long.parseLong(cmd.getOptionValue("T"));
        }

        ConsumerRecord<String, byte[]> record = null;
        if (cmd.hasOption("P")) {
            if (cmd.hasOption("O")) {
                record = consume(kafkaConsumer, topicName, Integer.parseInt(cmd.getOptionValue("P")), Long.parseLong(cmd.getOptionValue("O")), timeout);

            } else {

            }

        } else {
            latest(cmd, kafkaAdmin);
        }

        if (record != null) {
            log("Timestamp: " + DATE_FORMAT.format(new Date(record.timestamp())));
            log("Topic: " + record.topic());
            log("Partition: " + record.partition());
            log("Offset: " + record.offset());
            log("Key: " + record.key());
            log("Headers: " + toString(record.headers()));
            log("Message: ");
            log(prettyPrint(new String(record.value())));

        } else {
            log("Cannot get message from topic: " + topicName);
        }
    }

    public static void application(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        if (!cmd.hasOption("f")) {
            log("Jar files for Kafka application need to be specified using argument 'f'");
            System.exit(1);
        }

        if (!cmd.hasOption("s")) {
            log("Main class for Kafka application need to be specified using argument 's'");
            System.exit(1);
        }

        try {
            URLClassLoader ucl = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method m = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            m.setAccessible(true);

            String f = cmd.hasOption("f") ? cmd.getOptionValue("f") : null;
            String[] files = f.split(";");
            for (String fileName : files) {
                if (fileName.endsWith(".jar")) {
                    File jar = new File(streams, fileName);
                    m.invoke(ucl, jar.toURI().toURL());
                }
            }

            Class clazz = Class.forName(cmd.getOptionValue("s"));

            Method method = clazz.getMethod("main", new Class[]{String[].class});
            method.invoke(null, new Object[]{new String[0]});

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            System.exit(0);
        }
    }

    public static void stream(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        if (!cmd.hasOption("s")) {
            log("Class for Kafka Stream task need to be specified using argument 's'");
            System.exit(1);
        }

        try {
            URLClassLoader ucl = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method m = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            m.setAccessible(true);

            String f = cmd.hasOption("f") ? cmd.getOptionValue("f") : null;
            if (f != null) {
                String[] files = f.split(";");
                for (String fileName : files) {
                    if (fileName.endsWith(".jar")) {
                        File jar = new File(streams, fileName);
                        m.invoke(ucl, jar.toURI().toURL());
                    }
                }

            } else {
                for (File jarFile : streams.listFiles()) {
                    if (jarFile.isFile() && jarFile.getName().endsWith(".jar")) {
                        m.invoke(ucl, jarFile.toURI().toURL());
                    }
                }
            }

            Class clazz = Class.forName(cmd.getOptionValue("s"));
            String methodName = cmd.getOptionValue("t");

            Method method = clazz.getMethod(methodName, new Class[]{Properties.class});
            method.invoke(null, new Object[]{kafkaAdmin.createStreamConfiguration(clazz.getSimpleName())});

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            System.exit(0);
        }
    }

    public static void groovy(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        GroovyShell shell = new GroovyShell();
        if (cmd.hasOption("f")) {
            try {
                shell.parse(new File(scripts, cmd.getOptionValue("f")))
                        .invokeMethod(cmd.getOptionValue("t"), new Object[]{kafkaAdmin.createStreamConfiguration("GroovyApplication")});

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        String script = "return 'hello world'";

        Object result = shell.evaluate(script);
    }

    // Utility methods:
    private static void executeCommandLine(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        long timestamp = System.currentTimeMillis();
        String action = HELP;
        if (cmd.hasOption("h")) {
            action = HELP;

        } else if (cmd.hasOption("a")) {
            action = cmd.getOptionValue("a");

        } else {
            action = getDefaultAction(cmd);
        }

        if (action == null) {
            log("Cannot determine default action, please specify using '-c'");
        }

        log("############### Start executing '" + action + "' on environment '" + env + "' at " + DATE_FORMAT.format(new Date()) +
                " ###############");
        log("");

        try {
            Method method = KafkaAdmin.class.getMethod(action, new Class[]{CommandLine.class, KafkaAdmin.class});
            method.invoke(null, new Object[]{cmd, kafkaAdmin});

            log("");
            log("-------------- Execution succeeded, using " + (System.currentTimeMillis() - timestamp) + "ms ---------------");
            log("");

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log(e.getMessage());
            System.exit(1);
            log("");
            log("-------------- Execution failed at " + DATE_FORMAT.format(new Date()) + " ---------------");
        }
    }

    private static String getDefaultAction(CommandLine cmd) {
        String action = HELP;

        if (cmd.hasOption("b")) {
            Properties properties = getBodConfig(cmd);
            if (properties.containsKey("p") && properties.containsKey("c")
                    || cmd.hasOption("p") && properties.containsKey("c")
                    || properties.containsKey("p") && cmd.hasOption("c")) {
                action = PUB_AND_SUB;

            } else if (properties.containsKey("p")) {
                action = PRODUCE;

            } else if (properties.containsKey("c")) {
                action = LATEST;

            }


        } else if (cmd.hasOption("p")) {
            if (cmd.hasOption("c")) {
                action = PUB_AND_SUB;

            } else {
                action = PRODUCE;
            }

        } else if (cmd.hasOption("c")) {
            if (cmd.hasOption("n")) {
                action = CONSUME;
            } else {
                action = LATEST;
            }
        } else if (cmd.hasOption("Q")) {
            action = TOPICS;

        } else if (cmd.hasOption("q")) {
            action = TOPICS;

        }

        return action;
    }

    private static RecordMetadata produce(KafkaAdmin kafkaAdmin, String topicName, File file, String hs, String key) throws Exception {

        long timestamp = System.currentTimeMillis();
        if (!file.exists()) {
            log("File does not exist: " + file.getAbsolutePath());
            System.exit(1);
        }
        byte[] msg = FileUtils.readFileToByteArray(file);

        KafkaProducer kafkaProducer = kafkaAdmin.createKafkaProducer();
        RecordBuilder builder = new RecordBuilder(topicName).key(key).value(msg);
        if (hs != null) {
            Headers headers = headers(hs);
            headers.forEach(e -> {
                builder.header(e.key(), new String(e.value()));
            });
        }
        ProducerRecord<String, byte[]> record = builder.create();

        Future<RecordMetadata> future = kafkaProducer.send(record);
        while (!future.isDone()) {
            if (System.currentTimeMillis() - timestamp > 60000) {
                throw new TimeoutException("Fail to publish message to: " + topicName + " in 60 second.");
            }

            Thread.sleep(100L);
        }

        RecordMetadata metadata = future.get();

        log("Timestamp: " + DATE_FORMAT.format(new Date(metadata.timestamp())));
        log("Topic: " + metadata.topic());
        log("Partition: " + metadata.partition());
        log("Offset: " + metadata.offset());
        log("Key: " + record.key());
        log("Headers: " + toString(record.headers()));
        log("Message: " + new String(record.value()));

        return metadata;

    }

    private static ConsumerRecord<String, byte[]> latest(KafkaConsumer kafkaConsumer, String topicName, Collection<TopicPartition> partitions) throws Exception {
        List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();
        Map<TopicPartition, Long> latestOffsets = kafkaConsumer.endOffsets(partitions);
        for (TopicPartition partition : partitions) {
            List<TopicPartition> assignments = new ArrayList<>();
            assignments.add(partition);

            kafkaConsumer.assign(assignments);
            Long latestOffset = Math.max(0, latestOffsets.get(partition) - 1);
            kafkaConsumer.seek(partition, Math.max(0, latestOffset));
            ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(5000));

            polled.forEach(rc -> {
                records.add(rc);
            });
        }

        ConsumerRecord<String, byte[]> rc = null;
        for (ConsumerRecord<String, byte[]> record : records) {
            if (rc == null) {
                rc = record;
            } else if (record.timestamp() > rc.timestamp()) {
                rc = record;
            }
        }

        return rc;
    }

    private static ConsumerRecord<String, byte[]> consume(KafkaConsumer kafkaConsumer, String topicName, int partition, long offset, long timeout) {
        List<ConsumerRecord<String, byte[]>> records = new ArrayList<>();
        TopicPartition topicPartition = new TopicPartition(topicName, partition);

        Collection<TopicPartition> partitions = new ArrayList<>();
        partitions.add(topicPartition);
        kafkaConsumer.assign(partitions);
        kafkaConsumer.seekToBeginning(partitions);

        ConsumerRecords<String, byte[]> polled = kafkaConsumer.poll(Duration.ofMillis(timeout));
        polled.forEach(rc -> {
            if(rc.offset() == offset) {
                records.add(rc);
            }
        });

        return records.isEmpty()? null : records.get(0);
    }

    private static Properties getBodConfig(CommandLine cmd) {
        File dir = new File(workspace, cmd.getOptionValue("b"));
        File bod = new File(dir, "bod.properties");
        if (!bod.exists()) {
            log("File '" + bod + "' does not exit.");
        }

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(bod));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }

    private static String getBodProperty(String propName, Properties properties) {
        return properties.containsKey(env.toLowerCase() + "." + propName) ?
                properties.getProperty(env.toLowerCase(Locale.ROOT) + "." + propName) : properties.getProperty(propName);
    }

    private static Headers headers(String headers) {
        if (headers == null) {
            return null;
        }

        RecordHeaders recordHeaders = new RecordHeaders();
        if (headers.startsWith("[") && headers.endsWith("]")) {
            String[] arr = headers.substring(1, headers.length() - 1).split(";");
            for (String exp : arr) {
                if (exp.contains("=")) {
                    String[] kv = exp.split("=");
                    String key = kv[0].trim();
                    String value = kv[1].trim();
                    if (value.startsWith("'") && value.endsWith("'") || value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }

                    recordHeaders.add(key, value.getBytes());
                }
            }

        } else {
            String token = base64Decode(headers);
            JsonObject jsonObject = JsonParser.parseString(token).getAsJsonObject();

            jsonObject.entrySet().forEach(e -> {
                recordHeaders.add(new RecordHeader(e.getKey(), e.getValue().getAsString().getBytes()));
            });
        }

        return recordHeaders;
    }

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

    private static String base64Decode(String msg) {
        return new String(Base64.getDecoder().decode(msg));
    }

    private static String prettyPrint(String msg) {
        String token = msg.trim();
        if (token.startsWith("{") && token.endsWith("}") || token.startsWith("[") && token.endsWith("]")) {
            return prettyPrintJson(msg);
        }

        try {
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(token)));
            return prettyPrintXml(msg);
        } catch (Exception e) {

        }

        return msg;
    }

    private static String prettyPrintJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(JsonParser.parseString(json));
    }

    private static String prettyPrintXml(String xml) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult result = new StreamResult(new StringWriter());
        Source xmlInsetProperty = new StreamSource(new StringReader(xml));
        transformer.transform(xmlInsetProperty, result);
        return result.getWriter().toString();
    }

    private static String cellFormat(String value, int len) {
        char[] result = new char[len];
        char[] arr = value == null ? "null".toCharArray() : value.toCharArray();
        int size = arr.length;
        int length = Math.max(size, len);
        for (int i = 0; i < length; i++) {
            if (i < size) {
                result[i] = arr[i];
            } else {
                result[i] = ' ';
            }
        }
        return new String(result);
    }

    // RecordBuilder
    private static class PartitionStatus extends PartitionInfo {
        private transient TopicPartition topicPartition;
        private OffsetInfo offset = new OffsetInfo();

        public PartitionStatus(PartitionInfo partitionInfo) {
            super(partitionInfo.topic(), partitionInfo.partition(),
                    partitionInfo.leader(), partitionInfo.replicas(), partitionInfo.inSyncReplicas(), partitionInfo.offlineReplicas());
            this.topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
        }
    }

    private static class OffsetInfo {
        private long begin;
        private long end;
    }

    private static class RecordInfo implements Comparable<RecordInfo> {
        private ConsumerRecord<String, byte[]> record;

        private long ts;

        private int partition;
        private long offset;
        private String timestamp;
        private String key;
        private int size;
        private String headers;

        private RecordInfo(ConsumerRecord<String, byte[]> record) {
            this.ts = record.timestamp();
            this.partition = record.partition();
            this.offset = record.offset();
            this.timestamp = DATE_FORMAT.format(new Date(record.timestamp()));
            this.key = record.key();
            this.size = record.serializedValueSize();
            this.headers = record.headers() == null ? "" : KafkaAdmin.toString(record.headers());
        }

        @Override
        public int compareTo(RecordInfo o) {
            return (int) (ts - o.ts);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RecordInfo)) return false;

            RecordInfo that = (RecordInfo) o;

            if (partition != that.partition) return false;
            return offset == that.offset;
        }

        @Override
        public int hashCode() {
            int result = partition;
            result = 31 * result + (int) (offset ^ (offset >>> 32));
            return result;
        }
    }

    private static class RecordBuilder {
        private String topic;
        private Integer partition = 0;
        private Long timestamp;
        private String key;
        private byte[] value;
        private Map<String, String> headers = new HashMap<>();

        private RecordMetadata metadata;

        public RecordBuilder(String topic) {
            this.topic = topic;
        }

        public RecordBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public RecordBuilder partition(int partition) {
            this.partition = partition;
            return this;
        }

        public RecordBuilder generateKey() {
            this.key = UUID.randomUUID().toString();
            return this;
        }

        public RecordBuilder key(String key) {
            this.key = key;
            return this;
        }

        public RecordBuilder value(byte[] value) {
            this.value = value;
            return this;
        }

        public RecordBuilder message(String msg) {
            this.value = msg.getBytes();
            return this;
        }

        public RecordBuilder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public RecordBuilder metadata(RecordMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public ProducerRecord<String, byte[]> create() {
            RecordHeaders recordHeaders = new RecordHeaders();
            headers.entrySet().forEach(e -> {
                recordHeaders.add(new RecordHeader(e.getKey(), e.getValue().getBytes()));
            });

            return new ProducerRecord<String, byte[]>(topic, partition, key, value, recordHeaders);
        }
    }

}
