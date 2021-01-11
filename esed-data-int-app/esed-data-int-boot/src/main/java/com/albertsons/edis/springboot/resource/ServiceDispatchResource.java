package com.albertsons.edis.springboot.resource;

import com.albertsons.edis.ServiceDispatcher;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/dispatcher")
@Api(value = "Dispatch Service", hidden = false)
public class ServiceDispatchResource {

    @Autowired
    private ServiceDispatcher dispatcher;

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response list() {
        System.out.println("---------------- ");
        return Response.ok().build();
    }

    @POST
    @Path("invoke")
    public Response invoke() {
        return Response.ok().build();
    }
}
