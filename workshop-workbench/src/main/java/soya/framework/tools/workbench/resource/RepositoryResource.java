package soya.framework.tools.workbench.resource;

import com.google.gson.*;
import io.swagger.annotations.Api;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.tools.iib.*;
import soya.framework.tools.poi.XlsxUtils;
import soya.framework.tools.util.StringBuilderUtils;
import soya.framework.tools.workbench.configuration.BusinessObjectSchemaCache;
import soya.framework.tools.workbench.configuration.WorkbenchRepository;
import soya.framework.tools.xmlbeans.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Component
@Path("/repository")
@Api(value = "Repository Service", hidden = false)
public class RepositoryResource {
    @Autowired
    WorkbenchRepository repository;
    BusinessObjectSchemaCache schemaCache = BusinessObjectSchemaCache.getInstance();

    @GET
    @Path("/cmm/bod")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cmmBod() {
        return Response.ok(schemaCache.definedBusinessObjects()).build();
    }

    @POST
    @Path("/cmm/buffalo")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response buffalo(@HeaderParam("renderer") String renderer, String yaml) {
        if (renderer == null) {
            return Response.ok(Buffalo.fromYaml(yaml, XmlSchemaBase.class).render()).build();
        } else {
            return Response.ok(Buffalo.fromYaml(yaml, XmlSchemaBase.class).render(renderer)).build();

        }
    }

    @GET
    @Path("/cmm/xml/{bod}")
    @Produces(MediaType.APPLICATION_XML)
    public Response cmmSampleXml(@PathParam("bod") String bod) {
        if (!schemaCache.contains(bod)) {
            return Response.status(Response.Status.NOT_FOUND).build();

        } else {
            return Response.ok(schemaCache.generateXml(bod)).build();

        }
    }

    @GET
    @Path("/cmm/mappings/{bod}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cmmMappings(@PathParam("bod") String bod, String yaml) {
        if (!schemaCache.contains(bod)) {
            return Response.status(Response.Status.NOT_FOUND).build();

        } else {
            SchemaTypeSystem sts = schemaCache.getXmlSchemaTypeSystem(bod);
            XmlSchemaBase.Builder builder = XmlSchemaBase.builder().schemaTypeSystem(sts);

            builder.fromYaml(yaml);

            return Response.ok(builder.render(new JsonMappingRenderer())).build();

        }
    }

    @POST
    @Path("/cmm/esql")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response projectESQL(@HeaderParam("bod") String bod,
                                @HeaderParam("brokerSchema") String brokerSchema,
                                @HeaderParam("moduleName") String moduleName,
                                String flow) {
        if (!schemaCache.contains(bod)) {
            return Response.status(Response.Status.NOT_FOUND).build();

        } else {
            SchemaTypeSystem sts = schemaCache.getXmlSchemaTypeSystem(bod);
            XmlSchemaBase.Builder builder = XmlSchemaBase.builder().schemaTypeSystem(sts);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("source", "C:/Workshop/Repository/BusinessObjects/GroceryOrder/requirement/GroceryOrder_ERUMS_to_Canonical_Mapping_v1.2.1.xlsx");
            jsonObject.addProperty("sheet", "Mapping Source to Canonical");

            XlsxMappingAnnotator mappingAnnotator = new Gson().fromJson(jsonObject, XlsxMappingAnnotator.class);
            builder.annotate(mappingAnnotator);

            EsqlRenderer renderer = new EsqlRenderer();
            renderer.setBrokerSchema(brokerSchema);
            renderer.setModuleName(moduleName);

            return Response.ok(builder.render(renderer)).build();

        }
    }

    @GET
    @Path("/cmm/avro/{bod}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cmmAvro(@PathParam("bod") String bod) {
        if (!schemaCache.contains(bod)) {
            return Response.status(Response.Status.NOT_FOUND).build();

        } else {
            return Response.ok(schemaCache.getAvroSchema(bod).toString(true)).build();
        }
    }


    @GET
    @Path("/projects")
    @Produces(MediaType.APPLICATION_JSON)
    public Response projects() {
        return Response.ok(repository.projects()).build();
    }

    @GET
    @Path("/project/{project}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response project(@PathParam("project") String project) {
        File projectDir = repository.projectDirectory(project);
        File configFile = new File(projectDir, "build.properties");
        try {
            String contents = IOUtils.toString(new FileReader(configFile));
            return Response.ok(contents).build();

        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("/{project}/cmm/avro")
    @Produces(MediaType.APPLICATION_JSON)
    public Response projectCmmAvro(@PathParam("project") String project) {
        String bod = project;
        if (!schemaCache.contains(bod)) {
            if (!bod.startsWith("Get")) {
                bod = "Get" + project;
            }
        }

        if (!schemaCache.contains(bod) && !bod.endsWith("Type")) {
            bod = bod + "Type";
        }

        return Response.ok(schemaCache.getAvroSchema(bod).toString(true)).build();
    }

    @GET
    @Path("/{project}/requirement")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requirement(@PathParam("project") String project) {
        File projectDir = repository.projectDirectory(project);
        File configFile = new File(projectDir, "build.properties");
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(configFile));

            String req = properties.getProperty("project.requirement");
            File mappingFile = new File(repository.getRequirementHome(), req);

            JsonArray array = XlsxUtils.fromWorksheet(mappingFile, "Transformation");
            return Response.ok(array).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("{project}/mappings/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyze(@PathParam("project") String project) {
        String worksheet = "Transformation";
        String pathColumn = "Xpath";
        String valueColumn = "Expression";

        File projectDir = repository.projectDirectory(project);
        File configFile = new File(projectDir, "build.properties");
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(configFile));

            String req = properties.getProperty("project.requirement");
            File mappingFile = new File(repository.getRequirementHome(), req);

            JsonArray array = XlsxUtils.fromWorksheet(mappingFile, worksheet);

            JsonArray mappings = new JsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();

                if (o.get(pathColumn) != null && o.get(pathColumn).getAsString().trim().length() > 0) {
                    JsonObject mapping = new JsonObject();
                    mapping.add(pathColumn, o.get(pathColumn));
                    mapping.add("value", o.get(valueColumn));
                    if (o.get("Example") != null && o.get("Example").getAsString().trim().length() > 0) {
                        mapping.add("example", o.get("Example"));
                    }
                    mappings.add(mapping);
                }
            });

            Node root = NodeUtils.arrayToTree(mappings, pathColumn);
            return Response.status(200).entity(root).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @GET
    @Path("{project}/esql")
    @Produces(MediaType.TEXT_PLAIN)
    public Response esql(@PathParam("project") String project) {
        String worksheet = "Transformation";
        String pathColumn = "Xpath";
        String valueColumn = "Expression";

        File projectDir = repository.projectDirectory(project);
        File configFile = new File(projectDir, "build.properties");
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(configFile));

            ESQL esql = ESQL.builder()
                    .brokerSchema(properties.getProperty("project.iib.flow.broker.schema"))
                    .moduleName(properties.getProperty("project.iib.flow.name") + "_Compute")
                    .messageType(messageType(properties.getProperty("project.iib.flow.message.parser")))
                    .create();

            String req = properties.getProperty("project.requirement");
            File mappingFile = new File(repository.getRequirementHome(), req);

            JsonArray array = XlsxUtils.fromWorksheet(mappingFile, worksheet);

            JsonArray mappings = new JsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();

                if (o.get(pathColumn) != null && o.get(pathColumn).getAsString().trim().length() > 0) {
                    JsonObject mapping = new JsonObject();
                    mapping.add(pathColumn, o.get(pathColumn));
                    mapping.add("value", o.get(valueColumn));
                    mappings.add(mapping);
                }
            });

            Node root = NodeUtils.arrayToTree(mappings, pathColumn);

            return Response.ok(CmmESQLGenerators.createCmmESQLGenerator(root, "XML").generate(esql)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    private MessageType messageType(String value) {
        if ("json".equalsIgnoreCase(value)) {
            return MessageType.JSON;
        } else {
            return MessageType.XML;
        }
    }

    @GET
    @Path("/requirement")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requirements() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File dir = repository.getRequirementHome();
        File index = new File(dir, "README.md");
        try {
            List<String> lines = FileUtils.readLines(index, "UTF-8");
            JsonObject jsonObject = new JsonObject();
            JsonArray bos = new JsonArray();
            jsonObject.add("BusinessObject", bos);

            boolean boo = false;
            for (String ln : lines) {
                if (ln.trim().equals("## Business Objects")) {
                    boo = true;
                }

                if (true && ln.trim().startsWith("- ")) {
                    String bod = ln.substring(ln.indexOf("- ") + 2).trim();
                    bos.add(bod);
                    File boDir = new File(dir, bod);
                    if (!boDir.exists()) {
                        boDir.mkdir();
                    }

                    File boIndex = new File(boDir, "index.json");
                    if (!boIndex.exists()) {
                        boIndex.createNewFile();
                        JsonObject object = new JsonObject();
                        object.addProperty("bod", bod);
                        object.addProperty("url", "https://rxsafeway.sharepoint.com/sites/EnterpriseDataIntegrationServices/Shared%20Documents/Forms/AllItems.aspx");
                        object.addProperty("location", "/sites/EnterpriseDataIntegrationServices/Shared%20Documents/Analysis%20and%20Requirements%20(DIHD)/BusinessObjects/" + bod);

                        JsonObject mapping = new JsonObject();
                        object.add("mapping", mapping);
                        mapping.addProperty("file", "");
                        mapping.addProperty("worksheet", "");
                        mapping.addProperty("path", "");
                        mapping.addProperty("value", "");

                        String json = gson.toJson(object);
                        FileUtils.writeStringToFile(boIndex, json, "UTF-8");

                    }
                }

                if (boo && ln.startsWith("## ")) {
                    boo = false;
                }
            }

            return Response.ok(jsonObject).build();
        } catch (IOException e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/requirement/mapping-sheet")
    @Produces(MediaType.APPLICATION_JSON)
    public Response requirements(@HeaderParam("bo") String ob) {
        File dir = new File(repository.getRequirementHome(), ob);
        File index = new File(dir, "index.json");

        try {
            JsonObject jsonObject = (JsonObject) JsonParser.parseReader(new FileReader(index));
            JsonObject mapping = jsonObject.get("mapping").getAsJsonObject();
            String fileName = mapping.get("file").getAsString();
            String worksheet = mapping.get("worksheet").getAsString();

            File file = new File(dir, fileName);
            JsonArray arr = XlsxUtils.fromWorksheet(file, worksheet);

            return Response.ok(arr).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/requirement/worksheet")
    @Produces({MediaType.TEXT_PLAIN, MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response worksheet(@HeaderParam("bod") String bod, @HeaderParam("fileName") String fileName, @HeaderParam("worksheet") String worksheet) {
        try {
            File dir = new File(repository.getRequirementHome(), bod);
            File file = new File(dir, fileName);
            String contents = XlsxUtils.toString(file, worksheet);

            return Response.ok(contents).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/requirement/rowset")
    @Produces(MediaType.APPLICATION_XML)
    public Response rowsetXml(@HeaderParam("bod") String bod, @HeaderParam("fileName") String fileName, @HeaderParam("worksheet") String worksheet) {
        try {
            File dir = new File(repository.getRequirementHome(), bod);
            File file = new File(dir, fileName);
            JsonArray arr = XlsxUtils.fromWorksheet(file, worksheet);

            return Response.ok(rowsetXml(arr)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    private String rowsetXml(JsonArray array) {
        StringBuilder builder = new StringBuilder();
        StringBuilderUtils.println("<ROWSET>", builder);
        array.forEach(e -> {
            JsonObject object = e.getAsJsonObject();
            StringBuilderUtils.println("<ROW>", builder, 1);
            object.entrySet().forEach(en -> {
                if (en.getValue() != null) {
                    StringBuilderUtils.println("<" + en.getKey() + ">" + en.getValue().getAsString().trim() + "</" + en.getKey() + ">", builder, 2);
                }
            });

            StringBuilderUtils.println("</ROW>", builder, 1);
        });

        StringBuilderUtils.println("</ROWSET>", builder);
        return builder.toString();
    }

}
