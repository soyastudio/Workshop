package soya.framework.tools.workbench.resource;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import soya.framework.tools.aes.AES;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/aes")
@Api(value = "AES Service", hidden = false)
public class AesResource {

    @POST
    @Path("/AES/enc")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response encrypt(@HeaderParam("secret") String secret, String message) {
        String result = AES.encrypt(message, secret);
        return Response.status(200).entity(result).build();
    }

    @POST
    @Path("/AES/dec")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response decrypt(@HeaderParam("secret") String secret, String message) {
        String result = AES.decrypt(message, secret);
        return Response.status(200).entity(result).build();
    }
}
