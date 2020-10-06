package soya.framework.tools.workbench.resource;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import soya.framework.tools.util.StringCompressUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/utils")
@Api(value = "String Utils Service", hidden = false)
public class StringUtilsResource {

    @POST
    @Path("/encode")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response encode(String contents) {
        return Response.ok(StringCompressUtils.encode(contents)).build();
    }

    @POST
    @Path("/decode")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response decode(String contents) {
        return Response.ok(StringCompressUtils.decode(contents)).build();
    }
}
