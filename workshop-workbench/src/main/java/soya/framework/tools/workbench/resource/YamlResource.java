package soya.framework.tools.workbench.resource;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import soya.framework.tools.util.PropertiesUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/yaml")
@Api(value = "Yaml Service", hidden = false)
public class YamlResource {

    @POST
    @Path("/properties")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response yamlToProperties(String yaml) {
        PropertiesUtils.yamlToProperties(yaml);

        return Response.ok().build();
    }
}
