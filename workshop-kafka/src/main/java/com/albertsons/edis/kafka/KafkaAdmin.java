package com.albertsons.edis.kafka;


import org.apache.commons.cli.*;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.*;

import java.io.*;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class KafkaAdmin {

    private AdminClient adminClient;
    private KafkaProducer kafkaProducer;
    private KafkaConsumer kafkaConsumer;

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

    private static final String CREATE = "create";
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";
    private static final String CONSUME = "consume";
    private static final String PRODUCE = "produce";

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
                .required(true)
                .build());

        options.addOption(Option.builder("b")
                .longOpt("bod")
                .hasArg(true)
                .desc("Create business object test workspace.")
                .required(false)
                .build());

        options.addOption(Option.builder("p")
                .longOpt("inboundTopic")
                .hasArg(true)
                .desc("Publish to Inbound Topic, m required.")
                .required(false)
                .build());

        options.addOption(Option.builder("m")
                .longOpt("msg")
                .hasArg(true)
                .desc("Message for Kafka producer")
                .required(false)
                .build());

        options.addOption(Option.builder("c")
                .longOpt("outboundTopic")
                .hasArg(true)
                .desc("Consume from Outbound Topic")
                .required(false)
                .build());

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (!cmd.hasOption("a")) {
                createWorkspace(cmd, home);

            } else if(CREATE.equalsIgnoreCase(cmd.getOptionValue("a"))) {
                createWorkspace(cmd, home);

            } else if(CONSUME.equalsIgnoreCase(cmd.getOptionValue("a"))) {
                createWorkspace(cmd, home);

            } else if(PRODUCE.equalsIgnoreCase(cmd.getOptionValue("a"))) {
                createWorkspace(cmd, home);

            } else if(DELETE.equalsIgnoreCase(cmd.getOptionValue("a"))) {
                createWorkspace(cmd, home);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createWorkspace(CommandLine cmd, File home) throws IOException {

        String bo = cmd.getOptionValue("b");
        String p = cmd.getOptionValue("p");
        String c = cmd.getOptionValue("c");
        String m = cmd.getOptionValue("m");

        File workspace = new File(home, bo);
        workspace.mkdirs();

        File file = new File(workspace, "bod.properties");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file);
        writer.write("c=" + c + "\n");
        writer.write("p=" + p + "\n");
        writer.write("m=" + m + "\n");

        writer.flush();
        writer.close();

    }

    private static void updateWorkspace(CommandLine cmd, File home) throws IOException {
        System.out.println("---------------- update: ");
        String bo = cmd.getOptionValue("b");
        String p = cmd.getOptionValue("p");
        String c = cmd.getOptionValue("c");
        String m = cmd.getOptionValue("m");

        File workspace = new File(home, bo);
        workspace.mkdirs();

        File file = new File(workspace, "bod.properties");
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter writer = new FileWriter(file);
        writer.write("c=" + c + "\n");
        writer.write("p=" + p + "\n");
        writer.write("m=" + m + "\n");
        
        writer.flush();
        
        writer.close();

    }

    private static void delete(CommandLine cmd, File home) throws IOException {
        System.out.println("---------------- update: ");

        String bo = cmd.getOptionValue("b");
        String p = cmd.getOptionValue("p");
        String c = cmd.getOptionValue("c");
        String m = cmd.getOptionValue("m");

        File workspace = new File(home, bo);
        workspace.mkdirs();

        File file = new File(workspace, "bod.json");
        if (!file.exists()) {
            file.createNewFile();
        }

        File pub = new File(workspace, "publish.sh");
        if (!pub.exists()) {
            pub.createNewFile();
        }

    }

    private static void consume(CommandLine cmd, File home) throws IOException {
        System.out.println("---------------- update: ");
        String bo = cmd.getOptionValue("b");
        String p = cmd.getOptionValue("p");
        String c = cmd.getOptionValue("c");
        String m = cmd.getOptionValue("m");

        File workspace = new File(home, bo);
        workspace.mkdirs();

        File file = new File(workspace, "bod.json");
        if (!file.exists()) {
            file.createNewFile();
        }

        File pub = new File(workspace, "publish.sh");
        if (!pub.exists()) {
            pub.createNewFile();
        }

    }

    private static void produce(CommandLine cmd, File home) throws IOException {
        System.out.println("---------------- update: ");

        String bo = cmd.getOptionValue("b");
        String p = cmd.getOptionValue("p");
        String c = cmd.getOptionValue("c");
        String m = cmd.getOptionValue("m");

        File workspace = new File(home, bo);
        workspace.mkdirs();

        File file = new File(workspace, "bod.json");
        if (!file.exists()) {
            file.createNewFile();
        }

        File pub = new File(workspace, "publish.sh");
        if (!pub.exists()) {
            pub.createNewFile();
        }

    }
}
