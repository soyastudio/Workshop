package soya.framework.tools.workbench.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;
import soya.framework.tools.iib.*;
import soya.framework.tools.poi.XlsxUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

@Component
@Path("/iib")
@Api(value = "IIB Service", hidden = false)
public class IibResource {

    // ================================== Analyze Mapping Sheet
    @POST
    @Path("/mappings/url")
    @Produces(MediaType.APPLICATION_JSON)
    public Response xlsxToConsNode(@HeaderParam("url") String url,
                                   @HeaderParam("worksheet") String worksheet,
                                   @HeaderParam("pathColumn") String pathColumn,
                                   @HeaderParam("valueColumn") String valueColumn) {
        try {
            File file = new File(url);
            JsonArray array = XlsxUtils.fromWorksheet(file, worksheet);
            JsonArray mappings = new JsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                JsonObject mapping = new JsonObject();
                mapping.add(pathColumn, o.get(pathColumn));
                mapping.add(valueColumn, o.get(valueColumn));

                mappings.add(mapping);
            });

            Node root = NodeUtils.arrayToTree(mappings, pathColumn);
            return Response.status(200).entity(root).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/mappings/yaml")
    @Produces(MediaType.TEXT_PLAIN)
    public Response xlsxToYaml(@HeaderParam("url") String url,
                               @HeaderParam("worksheet") String worksheet,
                               @HeaderParam("pathColumn") String pathColumn,
                               @HeaderParam("valueColumn") String valueColumn) {
        try {
            File file = new File(url);
            JsonArray array = XlsxUtils.fromWorksheet(file, worksheet);
            JsonArray mappings = new JsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                JsonObject mapping = new JsonObject();
                mapping.add(pathColumn, o.get(pathColumn));
                mapping.add(valueColumn, o.get(valueColumn));

                mappings.add(mapping);
            });

            Node root = NodeUtils.arrayToTree(mappings, pathColumn);
            return Response.status(200).entity(NodeUtils.toYaml(root)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/mappings/upload")
    @Produces(MediaType.APPLICATION_JSON)
    public Response analyzeFromNodeUpload(@FormDataParam("file") InputStream fileInputStream,
                                          @FormDataParam("file") FormDataContentDisposition fileMetaData,
                                          @HeaderParam("worksheet") String worksheet,
                                          @HeaderParam("pathColumn") String pathColumn,
                                          @HeaderParam("valueColumn") String valueColumn) {
        try {
            JsonArray array = XlsxUtils.fromWorksheet(fileInputStream, worksheet);
            JsonArray mappings = new JsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                JsonObject mapping = new JsonObject();
                mapping.add(pathColumn, o.get(pathColumn));
                mapping.add(valueColumn, o.get(valueColumn));

                mappings.add(mapping);
            });

            Node root = NodeUtils.arrayToTree(mappings, pathColumn);
            return Response.status(200).entity(root).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================================== ESQL
    @POST
    @Path("/esql/yaml")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation("Generate ESQL from YAML")
    public Response generateFromYaml(@HeaderParam("brokerSchema") String brokerSchema,
                                     @HeaderParam("module") String module,
                                     @HeaderParam("inputType") String inputType,
                                     String yaml) {
        try {
            Node root = Node.fromYaml(yaml);
            return Response.ok(CmmESQLGenerators.createCmmESQLGenerator(root, inputType).generate(brokerSchema, module)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/esql/generate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation("Generate ESQL from Mapping Rules")
    public Response generateESQL(@HeaderParam("brokerSchema") String brokerSchema,
                                 @HeaderParam("moduleName") String moduleName,
                                 @HeaderParam("inputType") String inputType,
                                 String json) {
        try {
            return Response.ok(CmmESQLGenerators.createCmmESQLGenerator(Node.fromJson(json), inputType).generate(brokerSchema, moduleName)).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/esql/from/mappings")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response mappingToEsql(@HeaderParam("brokerSchema") String brokerSchema,
                                  @HeaderParam("module") String module,
                                  @HeaderParam("inputType") String inputType,
                                  String mappings) {
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(mappings));

            return Response.ok(CmmESQLGenerators.createCmmESQLGenerator(NodeUtils.fromProperties(properties), inputType).generate(brokerSchema, module)).build();

        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    @POST
    @Path("/esql/url")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation("Generate ESQL from excel through url")
    public Response xlsxUrlToESQL(@HeaderParam("url") String url,
                                  @HeaderParam("worksheet") String worksheet,
                                  @HeaderParam("settingColumn") String settingColumn,
                                  @HeaderParam("pathColumn") String pathColumn,
                                  @HeaderParam("valueColumn") String valueColumn,
                                  @HeaderParam("brokerSchema") String brokerSchema,
                                  @HeaderParam("moduleName") String moduleName,
                                  @HeaderParam("inputType") String inputType) {


        try {
            File file = new File(url);

            JsonArray array = XlsxUtils.fromWorksheet(file, worksheet);
            Set<String> settings = new LinkedHashSet<>();
            JsonArray mappings = new JsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                if (o.get(settingColumn) != null && o.get(settingColumn).getAsString().trim().length() > 0) {
                    String setting = o.get(settingColumn).getAsString();
                    String[] arr = setting.split(";");
                    for (String s : arr) {
                        settings.add(s.trim() + ";");
                    }

                } else if (o.get(pathColumn) != null && o.get(pathColumn).getAsString().trim().length() > 0) {
                    JsonObject mapping = new JsonObject();
                    mapping.add(pathColumn, o.get(pathColumn));
                    mapping.add(valueColumn, o.get(valueColumn));

                    mappings.add(mapping);

                }
            });

            Node root = NodeUtils.arrayToTree(mappings, pathColumn);

            return Response.status(200).entity(CmmESQLGenerators.createCmmESQLGenerator(root, inputType).generate(brokerSchema, moduleName, new ArrayList<String>(settings))).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/esql/upload")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiOperation("Generate ESQL through excel file upload")
    public Response xlsxToESQL(@FormDataParam("file") InputStream fileInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileMetaData,
                               @HeaderParam("worksheet") String worksheet,
                               @HeaderParam("pathColumn") String pathColumn,
                               @HeaderParam("valueColumn") String valueColumn,
                               @HeaderParam("brokerSchema") String brokerSchema,
                               @HeaderParam("moduleName") String moduleName,
                               @HeaderParam("inputType") String inputType) {

        try {
            JsonArray array = XlsxUtils.fromWorksheet(fileInputStream, worksheet);
            JsonArray mappings = new JsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                JsonObject mapping = new JsonObject();
                mapping.add(pathColumn, o.get(pathColumn));
                mapping.add(valueColumn, o.get(valueColumn));

                mappings.add(mapping);
            });

            Node root = NodeUtils.arrayToTree(mappings, pathColumn);
            return Response.ok(CmmESQLGenerators.createCmmESQLGenerator(root, inputType).generate(brokerSchema, moduleName)).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    // ================================== Transform
    @POST
    @Path("/transform/cmm")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_XML)
    public Response generateCmmXml(@HeaderParam("url") String url,
                                   @HeaderParam("worksheet") String worksheet,
                                   @HeaderParam("pathColumn") String pathColumn,
                                   @HeaderParam("valueColumn") String valueColumn) {
        try {
            File file = new File(url);
            JsonArray array = XlsxUtils.fromWorksheet(file, worksheet);
            JsonArray mappings = new JsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                JsonObject mapping = new JsonObject();
                mapping.add(pathColumn, o.get(pathColumn));
                mapping.add(valueColumn, o.get(valueColumn));

                mappings.add(mapping);
            });

            Node root = NodeUtils.arrayToTree(mappings, pathColumn);
            String xml = new CmmGenerator(root).generate();
            return Response.ok(xml).build();

        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/transform/json-to-xml")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_XML)
    public Response jsonToXml(@HeaderParam("url") String url,
                              @HeaderParam("worksheet") String worksheet,
                              @HeaderParam("pathColumn") String pathColumn,
                              @HeaderParam("valueColumn") String valueColumn,
                              String json) {
        try {
            File file = new File(url);

            JsonArray array = XlsxUtils.fromWorksheet(file, worksheet);
            JsonArray mappings = new JsonArray();
            array.forEach(e -> {
                JsonObject o = e.getAsJsonObject();
                JsonObject mapping = new JsonObject();
                mapping.add(pathColumn, o.get(pathColumn));
                mapping.add(valueColumn, o.get(valueColumn));

                mappings.add(mapping);
            });

            Node root = NodeUtils.arrayToTree(mappings, pathColumn);
            String xml = JsonToXmlTransformer.newInstance(root).generate(json);

            return Response.ok(xml).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(500).build();
        }
    }

    // ================================== Transform
    @POST
    @Path("/default/flow")
    @Produces(MediaType.APPLICATION_XML)
    public Response generateBasicMsgFlow(@HeaderParam("application") String application,
                                         @HeaderParam("brokerSchema") String brokerSchema,
                                         @HeaderParam("flowName") String flowName) {
        return Response.ok(IIBMsgFlowGenerator.generateMsgFlow(new Flow(application, brokerSchema, flowName))).build();
    }

    @POST
    @Path("/default/testflow")
    @Produces(MediaType.APPLICATION_XML)
    public Response generateBasicTestFlow(@HeaderParam("application") String application,
                                          @HeaderParam("brokerSchema") String brokerSchema,
                                          @HeaderParam("flowName") String flowName) {
        return Response.ok(IIBMsgFlowGenerator.generateTestFlow(new Flow(application, brokerSchema, flowName))).build();
    }

    @POST
    @Path("/default/subflow")
    @Produces(MediaType.APPLICATION_XML)
    public Response generateBasicSubflow(@HeaderParam("application") String application,
                                         @HeaderParam("brokerSchema") String brokerSchema,
                                         @HeaderParam("flowName") String flowName) {
        return Response.ok(IIBMsgFlowGenerator.generateSubFlow(new Flow(application, brokerSchema, flowName))).build();
    }

    @POST
    @Path("/flow")
    @Produces(MediaType.APPLICATION_XML)
    public Response generateIIBMessgeFlow(@HeaderParam("application") String application,
                                          @HeaderParam("brokerSchema") String brokerSchema,
                                          @HeaderParam("flowName") String flowName) {
        Flow flow = new Flow(application, brokerSchema, flowName);

        return Response.ok(flow.print()).build();
    }
}
