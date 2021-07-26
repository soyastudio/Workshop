package com.abs.edis.kafka;

import com.google.gson.*;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
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
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class EdisKafka {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Options COMMAND_LINE_OPTIONS;
    private static CommandLineParser COMMAND_LINE_PARSER = new DefaultParser();
    private static Map<String, Command> COMMANDS;
    private static Properties defaultProperties;

    private Context context;

    static {
        COMMANDS = new LinkedHashMap<>();
        Class<?>[] classes = EdisKafka.class.getDeclaredClasses();
        for (Class<?> c : classes) {
            if (Command.class.isAssignableFrom(c) && !c.isInterface()) {
                String name = c.getSimpleName();
                if (name.endsWith("Command")) {
                    name = name.substring(0, name.lastIndexOf("Command"));
                    try {
                        Command command = (Command) c.newInstance();
                        COMMANDS.put(name.toUpperCase(Locale.ROOT), command);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

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

        // Command line definition:
        COMMAND_LINE_OPTIONS = new Options();
        COMMAND_LINE_OPTIONS.addOption(Option.builder("a")
                .longOpt("action")
                .hasArg(true)
                .desc("Task to execute.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("b")
                .longOpt("businessObject")
                .hasArg(true)
                .desc("Business object.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("c")
                .longOpt("consumerTopic")
                .hasArg(true)
                .desc("Consumer Topic")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("d")
                .longOpt("dir")
                .hasArg(true)
                .desc("Directory")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("e")
                .longOpt("action")
                .hasArg(true)
                .desc("Environment, default 'local', case insensitive.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("f")
                .longOpt("file")
                .hasArg(true)
                .desc("File related to the action specified.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("H")
                .longOpt("header")
                .hasArg(true)
                .desc("Kafka message header")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("h")
                .longOpt("help")
                .hasArg(false)
                .desc("Help ([OPTIONAL])")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("k")
                .longOpt("key")
                .hasArg(true)
                .desc("Kafka message key")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("l")
                .longOpt("log")
                .hasArg(false)
                .desc("Enable log")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("m")
                .longOpt("msg")
                .hasArg(true)
                .desc("Message for Kafka producer")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("n")
                .longOpt("count")
                .hasArg(true)
                .desc("Number of message to print out.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("O")
                .longOpt("Offset")
                .hasArg(true)
                .desc("Offset.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("P")
                .longOpt("partition")
                .hasArg(true)
                .desc("Partition.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("p")
                .longOpt("producerTopic")
                .hasArg(true)
                .desc("Producer Topic.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("Q")
                .longOpt("Query")
                .hasArg(false)
                .desc("Find All Topics.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("q")
                .longOpt("query")
                .hasArg(true)
                .desc("Query.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("s")
                .longOpt("streamClass")
                .hasArg(true)
                .desc("Stream class to execute.")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("t")
                .longOpt("timestamp")
                .hasArg(true)
                .desc("Timestamp for polling records after or before")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("T")
                .longOpt("timeout")
                .hasArg(true)
                .desc("Timeout ")
                .required(false)
                .build());

        COMMAND_LINE_OPTIONS.addOption(Option.builder("x")
                .longOpt("cleanHistory")
                .hasArg(false)
                .desc("Clean log history during process.")
                .required(false)
                .build());
    }

    public EdisKafka(InputStream inputStream) {
        this.context = new Context(inputStream);
    }

    public String process(Node node) {
        Session session = createSession(node).init(context);
        try {
            process(session);
            return session.output;

        } catch (Exception e) {

            return e.getMessage();
        }

    }

    public void process(Session session) throws Exception {
        CommandLine cl = session.commandLine();
        String cmd = "CommandList";
        if (cl.hasOption("a")) {
            cmd = cl.getOptionValue("a");
        }

        Command command = COMMANDS.get(cmd.toUpperCase(Locale.ROOT));
        command.execute(session);

    }

    private Session session(Node node) {
        JsonObject jsonObject = estimate(node).getAsJsonObject();
        return GSON.fromJson(jsonObject, Session.class).init(context);
    }

    private Session createSession(Node node) {
        JsonObject jsonObject = estimate(node).getAsJsonObject();
        return GSON.fromJson(jsonObject, Session.class).init(context);
    }

    private static JsonElement estimate(Node node) {
        if (node.getTextContent() != null) {
            return new JsonPrimitive(node.getTextContent());

        } else if (node.getChildNodes().getLength() > 0) {
            if ("Item".equals(node.getFirstChild().getNodeName())) {
                JsonArray arr = new JsonArray();
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    arr.add(estimate(child));
                }

                return arr;

            } else {
                JsonObject obj = new JsonObject();
                NodeList list = node.getChildNodes();
                for (int i = 0; i < list.getLength(); i++) {
                    Node child = list.item(i);
                    obj.add(child.getNodeName(), estimate(child));
                }

                return obj;
            }
        }

        return null;

    }

    private static RecordMetadata send(KafkaProducer kafkaProducer, String topicName, byte[] msg, RecordHeaders headers, String key) throws Exception {

        long timestamp = System.currentTimeMillis();

        RecordBuilder builder = new RecordBuilder(topicName).key(key).value(msg);
        if (headers != null) {
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

    public static void main(String[] args) {

    }

    static class Context {
        private Properties configuration;

        private Context(InputStream inputStream) {
            configuration = new Properties();
            try {
                configuration.load(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public String getProperty(String propName, String env) {
            String key = env == null ? "LOCAL." + propName : env + "." + propName;
            if (configuration.containsKey(key)) {
                return configuration.getProperty(key);

            } else {
                return defaultProperties.getProperty(propName);

            }
        }

    }

    static class Session {
        private transient CommandLine cmd;

        private transient Properties adminProperties;
        private transient Properties producerProperties;
        private transient Properties consumerProperties;

        private String commandLine;
        private JsonElement input;
        private String output;

        private Session init(Context context) {
            List<String> list = new ArrayList<>();
            if (commandLine != null) {
                StringTokenizer tokenizer = new StringTokenizer(commandLine);
                while (tokenizer.hasMoreTokens()) {
                    list.add(tokenizer.nextToken());
                }
            }

            try {
                cmd = COMMAND_LINE_PARSER.parse(COMMAND_LINE_OPTIONS, list.toArray(new String[list.size()]));

            } catch (ParseException e) {
                throw new RuntimeException(e.getMessage());
            }

            String env = "LOCAL";
            if (cmd.hasOption("e")) {
                env = cmd.getOptionValue("e");
            }

            // Producer properties:
            this.producerProperties = new Properties();
            producerProperties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, context.getProperty(ProducerConfig.CLIENT_ID_CONFIG, env));
            producerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, context.getProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, env));
            producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, context.getProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, env));

            producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, context.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, env));
            producerProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, context.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, env));

            if ("SSL".equalsIgnoreCase(producerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
                producerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, context.getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, env));
                producerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, context.getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, env));
                producerProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, context.getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, env));
                producerProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, context.getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, env));

            } else if ("SASL_SSL".equalsIgnoreCase(producerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
                producerProperties.setProperty(SaslConfigs.SASL_MECHANISM, context.getProperty(SaslConfigs.SASL_MECHANISM, env));
                producerProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, context.getProperty(SaslConfigs.SASL_JAAS_CONFIG, env));
            }

            // Consumer properties:
            this.consumerProperties = new Properties();

            consumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, context.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, env));
            consumerProperties.setProperty(ConsumerConfig.CLIENT_ID_CONFIG, context.getProperty(ConsumerConfig.CLIENT_ID_CONFIG, env));
            consumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, context.getProperty(ConsumerConfig.GROUP_ID_CONFIG, env));
            consumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, context.getProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, env));
            consumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, context.getProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, env));
            consumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, context.getProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, env));
            consumerProperties.setProperty(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");

            consumerProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, context.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, env));
            if ("SSL".equalsIgnoreCase(consumerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
                consumerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, context.getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, env));
                consumerProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, context.getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, env));
                consumerProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, context.getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, env));
                consumerProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, context.getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, env));

            } else if ("SASL_SSL".equalsIgnoreCase(consumerProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
                consumerProperties.setProperty(SaslConfigs.SASL_MECHANISM, context.getProperty(SaslConfigs.SASL_MECHANISM, env));
                consumerProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, context.getProperty(SaslConfigs.SASL_JAAS_CONFIG, env));
            }

            // Admin properties:
            this.adminProperties = new Properties();
            adminProperties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, context.getProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, env));
            adminProperties.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, context.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, env));

            if ("SSL".equalsIgnoreCase(adminProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
                adminProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, context.getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, env));
                adminProperties.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, context.getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, env));
                adminProperties.setProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, context.getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, env));
                adminProperties.setProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, context.getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, env));

            } else if ("SASL_SSL".equalsIgnoreCase(adminProperties.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG))) {
                adminProperties.setProperty(SaslConfigs.SASL_MECHANISM, context.getProperty(SaslConfigs.SASL_MECHANISM, env));
                adminProperties.setProperty(SaslConfigs.SASL_JAAS_CONFIG, context.getProperty(SaslConfigs.SASL_JAAS_CONFIG, env));
            }

            return this;
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


        public CommandLine commandLine() {
            return cmd;
        }

        public JsonElement getInput() {
            return input;
        }

        public String getOutput() {
            return output;
        }

    }

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
        private JsonObject headers;

        private RecordInfo(ConsumerRecord<String, byte[]> record) {
            this.ts = record.timestamp();
            this.partition = record.partition();
            this.offset = record.offset();
            this.timestamp = DATE_FORMAT.format(new Date(record.timestamp()));
            this.key = record.key();
            this.size = record.serializedValueSize();
            this.headers = toJson(record.headers());
        }

        private JsonObject toJson(Headers headers) {
            JsonObject object = new JsonObject();
            headers.forEach(e -> {
                String key = e.key();
                String value = new String(e.value());
                object.addProperty(key, value);
            });
            return object;
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

    interface Command {
        void execute(Session session) throws Exception;
    }

    static class TopicListCommand implements Command {

        @Override
        public void execute(Session session) throws Exception {
            List<String> results = new ArrayList<>();
            CommandLine cmd = session.commandLine();
            String q = null;
            try {
                q = cmd.getOptionValue("q");

            } catch (Exception e) {

            }

            String query = q;

            AdminClient adminClient = session.createAdminClient();
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
                        results.add(e);

                    } else if (e.startsWith(query)) {
                        results.add(e);

                    }
                });

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

            session.output = GSON.toJson(results);
        }
    }

    static class ProduceCommand implements Command {

        @Override
        public void execute(Session session) throws Exception {
            long timestamp = System.currentTimeMillis();

            CommandLine cmd = session.commandLine();

            if (!cmd.hasOption("p")) {
                throw new IllegalArgumentException("Publish topic is required through option '-p'");
            }

            String topicName = cmd.getOptionValue("p");

            byte[] msg = GSON.toJson(session.input).getBytes(StandardCharsets.UTF_8);
            String hs = cmd.hasOption("H") ? cmd.getOptionValue("H") : null;
            String key = cmd.hasOption("k") ? cmd.getOptionValue("k") : UUID.randomUUID().toString();

            RecordMetadata metadata = send(session.createKafkaProducer(), topicName, msg, null, key);
            session.output = GSON.toJson(metadata);
        }
    }

    static class ConsumeCommand implements Command {

        @Override
        public void execute(Session session) throws Exception {
            CommandLine cmd = session.commandLine();

            if (!cmd.hasOption("c")) {
                throw new IllegalArgumentException("Consume topic is required through option '-c'");
            }

            String topicName = cmd.getOptionValue("c");

            KafkaConsumer<String, byte[]> kafkaConsumer = session.createKafkaConsumer();
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

            session.output = new String(rc.value());
        }
    }


}
