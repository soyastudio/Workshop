package soya.framework.tools.workbench.resource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.samskivert.mustache.Mustache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.tools.aes.AES;
import soya.framework.tools.workbench.configuration.BusinessObjectSchemaCache;
import soya.framework.tools.workbench.configuration.RepositoryConfiguration;
import soya.framework.tools.workbench.kafka.KafkaAdminService;
import soya.framework.tools.workbench.kafka.RecordModel;
import soya.framework.tools.xmlbeans.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileReader;
import java.util.*;

@Component
@Path("/workshop")
@Api(value = "Workshop Service", hidden = false)
public class WorkshopResource {

    @Autowired
    private KafkaAdminService kafkaAdminService;

    @Autowired
    RepositoryConfiguration configuration;
    BusinessObjectSchemaCache schemaCache = BusinessObjectSchemaCache.getInstance();

    @GET
    @Path("/index")
    @Produces(MediaType.APPLICATION_JSON)
    public Response index(@PathParam("path") String path) {
        return Response.ok(configuration).build();
    }

    @GET
    @Path("/cmm")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cmm() {
        Gson gson = new Gson();
        JsonObject root = new JsonObject();
        root.add("CMM", gson.toJsonTree(schemaCache.definedBusinessObjects()));
        JsonArray projects = new JsonArray();
        root.add("Projects", projects);
        File projectHome = configuration.getProjectHome();
        File[] files = projectHome.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                projects.add(f.getName());
            }
        }

        return Response.ok(root).build();
    }

    @GET
    @Path("/configuration")
    @Produces(MediaType.APPLICATION_JSON)
    public Response configuration() {
        List<String> list = new ArrayList<>();
        Properties properties = System.getProperties();
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            if (key.startsWith("soya.framework.workshop.")) {
                list.add(key);
            }
        }
        Collections.sort(list);

        Map<String, String> configuration = new LinkedHashMap<>();
        list.forEach(e -> {
            configuration.put(e, System.getProperty(e));
        });


        return Response.ok(configuration).build();
    }

    @GET
    @Path("/project")
    @Produces(MediaType.TEXT_HTML)
    public Response project(@QueryParam("bo") String bo) {
        try {
            File file = new File(configuration.getProjectHome(), bo + "/index.md");
            Parser parser = Parser.builder().build();
            org.commonmark.node.Node document = parser.parseReader(new FileReader(file));
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            return Response.ok(renderer.render(document)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/workflow")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response projectWorkflow(@QueryParam("bo") String bo, @QueryParam("renderer") String renderer) {
        try {
            File file = new File(configuration.getProjectHome(), bo + "/workflow.yaml");
            String yaml = IOUtils.toString(new FileReader(file));

            if (renderer == null) {
                return Response.ok(Buffalo.fromYaml(yaml, XmlSchemaBase.class).render()).build();
            } else {
                return Response.ok(Buffalo.fromYaml(yaml, XmlSchemaBase.class).render(renderer)).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("/workflow")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response workflow(@HeaderParam("renderer") String renderer, String yaml) {
        if (renderer == null) {
            return Response.ok(Buffalo.fromYaml(yaml, XmlSchemaBase.class).render()).build();
        } else {
            return Response.ok(Buffalo.fromYaml(yaml, XmlSchemaBase.class).render(renderer)).build();
        }
    }

    @GET
    @Path("/mustache")
    @Produces(MediaType.APPLICATION_JSON)
    public Response parseMustache(@HeaderParam("template") String template) {
        MustacheVariableVisitor visitor = new MustacheVariableVisitor();
        Mustache.compiler().compile(WorkshopRepository.getResourceAsString(template)).visit(visitor);
        return Response.ok(visitor.getVariables()).build();
    }

    @POST
    @Path("/mustache")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response mustache(@HeaderParam("template") String template, String data) {
        JsonObject jsonObject = JsonParser.parseString(data).getAsJsonObject();
        String result = Mustache.compiler().compile(WorkshopRepository.getResourceAsString(template)).execute(JsonUtils.toMap(jsonObject));

        return Response.ok(result).build();
    }

    @POST
    @Path("/aes/encrypt")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.TEXT_PLAIN)
    public Response encrypt(@HeaderParam("key") String key, String data) {
        String result = AES.encrypt(data, key);
        return Response.ok(result).build();
    }

    @POST
    @Path("/aes/decrypt")
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces(MediaType.TEXT_PLAIN)
    public Response decrypt(@HeaderParam("key") String key, String data) {
        String result = AES.decrypt(data, key);
        return Response.ok(result).build();
    }

    @POST
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_XML)
    @ApiOperation(value = "publish")
    public Response publish(@HeaderParam("inboundTopic") String inboundTopic, @HeaderParam("outboundTopic") String outboundTopic, String input) {
        RecordModel recordModel = kafkaAdminService.publish(inboundTopic, input);

        List<ConsumerRecord<String, byte[]>> records = kafkaAdminService.getLatestRecords(outboundTopic, 2);
        if (records.isEmpty()) {
            return Response.status(200).build();
        } else {
            String message = new String(records.get(records.size() - 1).value());
            return Response.status(200).entity(message).build();
        }
    }

}
