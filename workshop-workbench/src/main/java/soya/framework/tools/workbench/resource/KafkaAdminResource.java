package soya.framework.tools.workbench.resource;


import com.google.gson.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.tools.workbench.kafka.KafkaAdminService;
import soya.framework.tools.workbench.kafka.NewTopicModel;
import soya.framework.tools.workbench.kafka.RecordModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Component
@Path("/kafka")
@Api(value = "Kafka Admin Service")
public class KafkaAdminResource {
    @Autowired
    private KafkaAdminService kafkaAdminService;

    @GET
    @Path("/admin/metrics")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Kafka Metrics")
    public Response metrics() {
        Gson gson = new Gson();
        return Response.status(200).entity(gson.toJson(kafkaAdminService.metrics())).build();
    }

    @GET
    @Path("/admin/cluster")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Kafka Cluster")
    public Response cluster() {
        return Response.status(200).entity(kafkaAdminService.cluster()).build();
    }

    @GET
    @Path("/admin/consumer-groups")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Consumer Groups")
    public Response consumerGroups() {
        try {
            return Response.status(200).entity(kafkaAdminService.consumerGroups()).build();

        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/admin/topics")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Topics")
    public Response topics() {
        try {
            List<String> list = new ArrayList<>(kafkaAdminService.topicNames());
            Collections.sort(list);
            return Response.status(200).entity(list).build();

        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/admin/create-topic")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create Topic")
    public Response createTopic(String json) {
        kafkaAdminService.createTopic(NewTopicModel.fromJson(json));
        return Response.status(200).build();
    }

    @GET
    @Path("/admin/topic-info")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Topic Information")
    public Response topic(@QueryParam("topic") String topic) {
        List<PartitionInfo> partitions = kafkaAdminService.topic(topic);
        return Response.status(200).entity(partitions).build();
    }

    @DELETE
    @Path("/topic/delete")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Delete Topic")
    public Response deleteTopic(String json) {
        kafkaAdminService.deleteTopic(json);
        return Response.status(200).build();
    }

    @POST
    @Path("/producer/send")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Send Message")
    public Response publish(@HeaderParam("topic") String topic, String message) {

        RecordModel recordModel = kafkaAdminService.publish(topic, message);
        return Response.status(200).entity(recordModel).build();
    }

    @GET
    @Path("/consumer/get")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get Latest Message from Topic")
    public Response latestRecord(@HeaderParam("topic") String topic) {
        List<ConsumerRecord<String, byte[]>> records = kafkaAdminService.getLatestRecords(topic, 2);
        if (records.isEmpty()) {
            return Response.status(200).build();

        } else {
            String message = new String(records.get(records.size() - 1).value());
            return Response.status(200).entity(message).build();
        }
    }

    @POST
    @Path("/producer/sendWithHeader")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Send Message")
    public Response publishWitHeader(@HeaderParam("topic") String topic, @HeaderParam("headers") String headers, String message) {
        Map<String, String> map = new LinkedHashMap<>();
        if(headers != null) {
            JsonObject obj = JsonParser.parseString(headers).getAsJsonObject();
            obj.entrySet().forEach(en -> {
                map.put(en.getKey(), en.getValue().getAsString());
            });

        }

        RecordModel recordModel = kafkaAdminService.publish(topic, message, map);
        return Response.status(200).entity(recordModel).build();
    }

    @GET
    @Path("/consumer/getHeader")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get Headers of Latest Message from Topic")
    public Response latestRecordHeader(@HeaderParam("topic") String topic) {
        List<ConsumerRecord<String, byte[]>> records = kafkaAdminService.getLatestRecords(topic, 2);
        if (records.isEmpty()) {
            return Response.status(200).build();

        } else {
            JsonObject result = new JsonObject();
            Headers headers = records.get(records.size() - 1).headers();
            Header[] array = headers.toArray();
            for(Header header : array) {
                String key = header.key();
                String value = new String(header.value());
                result.add(key, new JsonPrimitive(value));
            }
            return Response.status(200).entity(result).build();
        }
    }

    @GET
    @Path("/consumer/list")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "List Messages from Topic")
    public Response latestRecords(@HeaderParam("topic") String topic, @HeaderParam("count") int count) {
        List<ConsumerRecord<String, byte[]>> records = kafkaAdminService.getLatestRecords(topic, count);
        List<RecordModel> models = new ArrayList<>();
        records.forEach(e -> {
            models.add(RecordModel.fromConsumerRecord(e));
        });
        return Response.status(200).entity(models).build();
    }
}
