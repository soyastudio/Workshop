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
import org.apache.kafka.clients.producer.*;
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
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS XXX");

    private static Properties defaultProperties;

    private static final String CREATE = "create";
    private static final String UPDATE = "update";
    private static final String FILE = "file";
    private static final String DELETE = "delete";

    private static final String CONSUME = "consume";
    private static final String PRODUCE = "produce";

    private static final String METRICS = "metrics";
    private static final String TOPICS = "topics";
    private static final String LATEST = "latest";
    private static final String PUB_AND_SUB = "pub-sub";
    private static final String APPLICATION = "application";
    private static final String STREAM = "stream";
    private static final String GROOVY = "groovy";
    private static final String UNDEPLOY = "undeploy";

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
        System.out.println("Create KafkaAdmin for environment: " + env);

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

        consumerProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG));
        if ("SSL".equalsIgnoreCase(consumerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            consumerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            consumerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            consumerProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            consumerProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(producerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
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

        } else if ("SASL_SSL".equalsIgnoreCase(producerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
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

        } else if ("SASL_SSL".equalsIgnoreCase(producerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
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
        if(configFile.exists()) {
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
                .hasArg(false)
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

        // Environment:
        if(cmd.hasOption("e")) {
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

        if("SSL".equalsIgnoreCase(getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            System.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            System.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            System.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            System.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));

        } else if ("SASL_SSL".equalsIgnoreCase(getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
            System.setProperty(SaslConfigs.SASL_MECHANISM, getProperty(SaslConfigs.SASL_MECHANISM));
            System.setProperty(SaslConfigs.SASL_JAAS_CONFIG, getProperty(SaslConfigs.SASL_JAAS_CONFIG));
        }

        // Context:
        if (cmd.hasOption("b")) {
            processBusinessObjectTask(cmd, new KafkaAdmin(), workspace);

        } else {
            processKafkaAdminTask(cmd, new KafkaAdmin());

        }
    }

    private static String getProperty(String propName) {
        if(System.getProperty("kafka." + propName) != null) {
            return System.getProperty("kafka." + propName);
        }

        String key = env + "." + propName;
        if(configuration.containsKey(key)) {
            return configuration.getProperty(key);
        }

        return defaultProperties.getProperty(propName);
    }

    // Global methods:
    private static void processKafkaAdminTask(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        System.out.println();
        String action = PRODUCE;
        if (!cmd.hasOption("a")) {
            if (cmd.hasOption("p")) {
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
        } else {
            action = cmd.getOptionValue("a");
        }

        switch (action) {
            case METRICS:
                metrics(cmd, kafkaAdmin);
                break;

            case TOPICS:
                topics(cmd, kafkaAdmin);
                break;

            case PRODUCE:
                kafkaProduce(cmd, kafkaAdmin);
                break;

            case LATEST:
                latest(cmd, kafkaAdmin);
                break;

            case CONSUME:
                kafkaConsume(cmd, kafkaAdmin);
                break;

            case PUB_AND_SUB:
                pubAndSub(cmd, kafkaAdmin);
                break;

            case APPLICATION:
                application(cmd, kafkaAdmin);
                break;

            case STREAM:
                stream(cmd, kafkaAdmin);
                break;

            case GROOVY:
                groovy(cmd, kafkaAdmin);
        }
    }

    private static void metrics(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        System.out.println("==================== Metrics ====================");
        List<Metric> metricList = new ArrayList<>(kafkaAdmin.createAdminClient().metrics().values());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(metricList));
        System.out.println();
    }

    private static void topics(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        System.out.println("==================== List Topic Names ====================");
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
                    System.out.println(e);

                } else if (e.startsWith(query)) {
                    System.out.println(e);

                }
            });

            System.out.println();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static RecordMetadata kafkaProduce(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        System.out.println("==================== Produce Message ====================");

        String topicName = cmd.hasOption("p") ? cmd.getOptionValue("p") : null;
        if (topicName == null) {
            System.out.println("ERROR: Topic is not set. Please set topic name using parameter 'p' or 't'");
            System.exit(1);
        }

        String message = "";
        if (!cmd.hasOption("m")) {
            System.out.println("ERROR: Message is not set. Please set message using parameter 'm'");
            System.exit(1);

        } else {
            message = base64Decode(cmd.getOptionValue("m"));
        }

        String hs = cmd.hasOption("H") ? cmd.getOptionValue("H") : null;

        KafkaProducer kafkaProducer = kafkaAdmin.createKafkaProducer();
        RecordBuilder builder = new RecordBuilder(topicName).generateKey().message(message);
        if (hs != null) {
            Headers headers = headers(hs);
            headers.forEach(e -> {
                builder.header(e.key(), new String(e.value()));
            });
        }
        ProducerRecord<String, byte[]> record = builder.create();

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

            System.out.println("Timestamp: " + DATE_FORMAT.format(new Date(metadata.timestamp())));
            System.out.println("Topic: " + metadata.topic());
            System.out.println("Partition: " + metadata.partition());
            System.out.println("Offset: " + metadata.offset());
            System.out.println("Headers: " + toString(record.headers()));
            System.out.println("Key: " + record.key());
            System.out.println("Message: " + new String(record.value()));
            System.out.println();

            return metadata;

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private static void kafkaConsume(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        System.out.println("==================== Consume Message ====================");

        String kafkaTopicName = cmd.getOptionValue("c");
        int count = 20;
        if (cmd.hasOption("n")) {
            count = Integer.parseInt(cmd.getOptionValue("n"));
        }

        List<ConsumerRecord<String, byte[]>> results = consume(kafkaAdmin, kafkaTopicName, count);
        if (results.size() > 0) {
            results.forEach(rc -> {
                //if(rc.timestamp() > timestamp) {
                System.out.println("Timestamp: " + DATE_FORMAT.format(new Date(rc.timestamp())));
                System.out.println("Topic: " + rc.topic());
                System.out.println("Partition: " + rc.partition());
                System.out.println("Offset: " + rc.offset());
                System.out.println("Headers: " + toString(rc.headers()));
                System.out.println("Key: " + rc.key());
                System.out.println("Message: " + new String(rc.value()));
                System.out.println();
                //}
            });
        }

    }

    private static void latest(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        System.out.println("==================== Consume Latest Message ====================");

        String kafkaTopicName = cmd.getOptionValue("c");
        int count = 2;

        List<ConsumerRecord<String, byte[]>> results = consume(kafkaAdmin, kafkaTopicName, count);
        if (results.size() > 0) {
            ConsumerRecord<String, byte[]> rc = results.get(0);

            System.out.println("Timestamp: " + DATE_FORMAT.format(new Date(rc.timestamp())));
            System.out.println("Topic: " + rc.topic());
            System.out.println("Partition: " + rc.partition());
            System.out.println("Offset: " + rc.offset());
            System.out.println("Headers: " + toString(rc.headers()));
            System.out.println("Key: " + rc.key());
            System.out.println("Message: " + prettyPrint(new String(rc.value())));
            System.out.println();

        } else {
            System.out.println("There are no messages on topic " + kafkaTopicName);
            System.out.println();
        }
    }

    private static void pubAndSub(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        RecordMetadata metadata = kafkaProduce(cmd, kafkaAdmin);
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long timestamp = metadata.timestamp();
        latest(cmd, kafkaAdmin);
    }

    private static void application(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        if (!cmd.hasOption("f")) {
            System.out.println("Jar files for Kafka application need to be specified using argument 'f'");
            System.exit(1);
        }

        if (!cmd.hasOption("s")) {
            System.out.println("Main class for Kafka application need to be specified using argument 's'");
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

    private static void stream(CommandLine cmd, KafkaAdmin kafkaAdmin) {
        if (!cmd.hasOption("s")) {
            System.out.println("Class for Kafka Stream task need to be specified using argument 's'");
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

    private static void groovy(CommandLine cmd, KafkaAdmin kafkaAdmin) {
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
        System.out.println(result);
    }

    // Business Object Tasks:
    private static void processBusinessObjectTask(CommandLine cmd, KafkaAdmin kafkaAdmin, File home) {

        File dir = new File(home, cmd.getOptionValue("b"));
        File bod = new File(dir, "bod.properties");
        String action = cmd.hasOption("a") ? cmd.getOptionValue("a") : null;
        if (action == null) {
            action = PRODUCE;

        }

        int exit = 0;

        File logFile = new File(dir, "test.log");
        boolean cleanLogHistory = cmd.hasOption("x");

        StringBuilder logger = new StringBuilder();

        System.out.println();
        logger.append("==================== Executing '").append(action).append("' on ").append(new Date()).append(" ====================").append("\n");

        try {
            if (CREATE.equalsIgnoreCase(action)) {
                createWorkspace(cmd, dir);

            } else if (UPDATE.equalsIgnoreCase(action)) {
                updateWorkspace(cmd, dir);

            } else if (FILE.equalsIgnoreCase(action)) {
                processFile(cmd, dir);

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
            String result = logger.toString();
            System.out.println(result);

            if (logFile.exists()) {
                try {
                    if (cmd.hasOption("x")) {
                        Files.write(Paths.get(logFile.toURI()), result.getBytes(StandardCharsets.UTF_8));
                    } else {
                        Files.write(Paths.get(logFile.toURI()), result.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.exit(exit);
        }
    }

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

    private static void processFile(CommandLine cmd, File dir) throws IOException {
        workspaceAvailable(dir);

        String f = cmd.getOptionValue("f");
        String m = cmd.getOptionValue("m");

        File file = new File(dir, f);
        if (!file.exists()) {
            FileUtils.forceMkdirParent(file);
            file.createNewFile();
        }

        if ("X".equalsIgnoreCase(m)) {
            FileUtils.forceDelete(file);

        } else {
            String contents = base64Decode(m);
            FileWriter writer = new FileWriter(file);
            writer.write(contents);

            writer.flush();
            writer.close();

        }
    }

    private static void deleteWorkspace(CommandLine cmd, File dir) throws IOException {
        FileUtils.forceDelete(dir);
    }

    private static void consume(CommandLine cmd, File dir, KafkaAdmin kafkaAdmin, StringBuilder logger) throws IOException {
        logger.append("Kafka Consumer:").append("\n");

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

        long timestamp = System.currentTimeMillis();

        Properties bod = new Properties();
        bod.load(new FileInputStream(new File(dir, "bod.properties")));

        String p = cmd.hasOption("p") ? cmd.getOptionValue("p") : bod.getProperty("p");
        String c = cmd.hasOption("c") ? cmd.getOptionValue("c") : bod.getProperty("c");

        if (p != null) {
            String msg = null;
            if (cmd.hasOption("f")) {
                String f = cmd.getOptionValue("f");
                msg = IOUtils.toString(new FileInputStream(new File(dir, f)), Charset.defaultCharset());
            }

            if (msg == null && cmd.hasOption("m")) {
                msg = base64Decode(cmd.getOptionValue("m"));
            }

            if (msg == null && bod.getProperty("m") != null) {
                msg = base64Decode(bod.getProperty("m"));
            }

            if (msg == null) {
                System.out.println("No message to publish!");
                System.exit(1);
            }

            String hs = cmd.hasOption("H") ? cmd.getOptionValue("H") : null;

            KafkaProducer kafkaProducer = kafkaAdmin.createKafkaProducer();
            RecordBuilder builder = new RecordBuilder(p).generateKey().message(msg);
            if (hs != null) {
                Headers headers = headers(hs);
                headers.forEach(e -> {
                    builder.header(e.key(), new String(e.value()));
                });
            }
            ProducerRecord<String, byte[]> record = builder.create();

            Future<RecordMetadata> future = kafkaProducer.send(record);
            while (!future.isDone()) {
                if(System.currentTimeMillis() - timestamp > 30000l) {
                    throw new RuntimeException(new TimeoutException());
                }

                try {
                    Thread.sleep(100L);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                RecordMetadata metadata = future.get();
                timestamp = metadata.timestamp();

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
        }

        try {
            Thread.sleep(10000l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (c != null) {
            List<ConsumerRecord<String, byte[]>> list = consume(kafkaAdmin, c, 2);
            ConsumerRecord<String, byte[]> rc = null;
            if (timestamp > 0 && list.size() > 0 && list.get(0).timestamp() > timestamp) {
                rc = list.get(0);
                logger.append("Timestamp: ").append(DATE_FORMAT.format(new Date(rc.timestamp()))).append("\n");
                logger.append("Topic: ").append(rc.topic()).append("\n");
                logger.append("Partition: ").append(rc.partition()).append("\n");
                logger.append("Offset: ").append(rc.offset()).append("\n");
                logger.append("Headers: ").append(toString(rc.headers())).append("\n");
                logger.append("Key: ").append(rc.key()).append("\n");
                logger.append("Message: ").append(prettyPrint(new String(rc.value()))).append("\n");

            } else {
                logger.append("No recorder received.");
            }

            logger.append("\n");
        }
    }

    // utility methods:
    private static void workspaceAvailable(File dir) {
        if (!dir.exists()) {
            System.out.println();
            System.out.println("Workspace is not created for business object '" + dir.getName() + "'.");
            System.exit(1);
        }
    }

    private static List<ConsumerRecord<String, byte[]>> consume(KafkaAdmin kafkaAdmin, String topicName, int count) {

        KafkaConsumer<String, byte[]> kafkaConsumer = kafkaAdmin.createKafkaConsumer();

        List<PartitionInfo> partitionInfoSet = kafkaConsumer.partitionsFor(topicName);
        Collection<TopicPartition> partitions = partitionInfoSet.stream()
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

        List<ConsumerRecord<String, byte[]>> results = rawRecords
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

        Collections.reverse(results);

        return results;
    }

    private static Headers headers(String headers) {
        if (headers == null) {
            return null;
        }

        String token = base64Decode(headers);
        JsonObject jsonObject = JsonParser.parseString(token).getAsJsonObject();
        RecordHeaders recordHeaders = new RecordHeaders();
        jsonObject.entrySet().forEach(e -> {
            recordHeaders.add(new RecordHeader(e.getKey(), e.getValue().getAsString().getBytes()));
        });

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

    //
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
