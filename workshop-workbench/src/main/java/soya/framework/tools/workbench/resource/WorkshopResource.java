package soya.framework.tools.workbench.resource;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import soya.framework.tools.workbench.configuration.RepositoryConfiguration;
import soya.framework.tools.iib.Node;
import soya.framework.tools.iib.NodeUtils;
import soya.framework.tools.xmlbeans.XmlBeansUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Path("/workshop")
@Api(value = "Workshop Service", hidden = false)
public class WorkshopResource {
    @Autowired
    RepositoryConfiguration configuration;

    @GET
    @Path("/schema")
    @Produces(MediaType.APPLICATION_JSON)
    public Response schema() {
        try {
            List<String> list = new ArrayList<>();
            File dir = new File(configuration.getHome(), "CMM/BOD");
            for (File file : dir.listFiles()) {
                if (file.getName().endsWith(".xsd")) {
                    list.add(file.getName());
                }
            }

            Collections.sort(list);
            return Response.status(200).entity(list).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }


    @POST
    @Path("/schema/{bod}")
    @Produces(MediaType.APPLICATION_XML)
    public Response schema(@PathParam("bod") String bod) {
        try {
            File dir = new File(configuration.getHome(), "CMM/BOD");
            File file = new File(dir, bod + ".xsd");

            return Response.status(200).entity(XmlBeansUtils.xsdToXml(file)).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }


    @POST
    @Path("/excel/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response excelToTree(@HeaderParam("url") String url, @HeaderParam("worksheet") String worksheet, @HeaderParam("pathColumn") String pathColumn) {
        try {
            File file = new File(url);
            Node result = NodeUtils.fromExcelToProcessTree(file, worksheet, pathColumn);
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/excel/yaml")
    @Produces(MediaType.TEXT_PLAIN)
    public Response excelToYaml(@HeaderParam("url") String url, @HeaderParam("worksheet") String worksheet, @HeaderParam("pathColumn") String pathColumn) {
        try {
            File file = new File(url);
            Node result = NodeUtils.fromExcelToProcessTree(file, worksheet, pathColumn);
            return Response.status(200).entity(NodeUtils.toYaml(result)).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/excel/input-schema")
    @Produces(MediaType.APPLICATION_XML)
    public Response reverseInputSchemaFromProperties(@HeaderParam("url") String url, @HeaderParam("worksheet") String worksheet) {
        try {
            File file = new File(url);
            String result = NodeUtils.reverseInputSchema(file, worksheet);
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }
}
