package soya.framework.tools.workbench.resource;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import soya.framework.tools.iib.NodeUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.Properties;

@Component
@Path("/xpath")
@Api(value = "XPath Service", hidden = false)
public class XPathResource {

    @POST
    @Path("/properties-to-tree")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response propertiesToTree(String mappings) {
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(mappings));
            JsonObject result = NodeUtils.fromPropertiesToProcessTree(properties);
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/reverse-from-properties")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reverseInputFromProperties(String mappings) {
        try {
            Properties properties = new Properties();
            properties.load(new StringReader(mappings));
            JsonObject result = NodeUtils.reverseInputTree(properties);
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/reverse-from-array")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response reverseInputFromArray(String jsonArray) {
        try {
            JsonObject result = NodeUtils.reverseInputTree(JsonParser.parseString(jsonArray).getAsJsonArray());
            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }
}
