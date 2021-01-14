package com.albertsons.edis.springboot.resource;

import com.albertsons.edis.ServiceDispatcher;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/dispatcher")
@Api(value = "Dispatch Service", hidden = false)
public class ServiceDispatchResource {

    @Autowired
    private ServiceDispatcher dispatcher;

    @GET
    @Path("/service")
    @Produces(MediaType.APPLICATION_JSON)
    public Response services() {
        return Response.ok(serviceRegistry().getServices()).build();
    }

    @GET
    @Path("/service/{serviceName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response operations(@PathParam("serviceName") String serviceName) {
        return Response.ok(serviceRegistry().getOperations(serviceName)).build();
    }

    @GET
    @Path("/service/{serviceName}/{operationName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response operations(@PathParam("serviceName") String serviceName, @PathParam("operationName") String operationName) {
        return Response.ok(serviceRegistry().getOperations(serviceName)).build();
    }

    @POST
    @Path("invoke/{serviceName}/{operationName}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})
    public Response invoke(@PathParam("serviceName") String serviceName, @PathParam("operationName") String operationName, String payload) {
        try {
            Object result = dispatcher.dispatch(serviceName, operationName, payload);

            return Response.ok(result).build();

        } catch (Exception exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(exception.getCause().getMessage()).build();
        }

    }

    private ServiceDispatcher.ServiceRegistry serviceRegistry() {
        return (ServiceDispatcher.ServiceRegistry) dispatcher;
    }
 }
