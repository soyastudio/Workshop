package com.albertsons.edis.springboot.resource;

import com.albertsons.edis.PipelineProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/pipeline")
public class PipelineResource {

    @Autowired
    private PipelineProcessService service;

    @GET
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response list() {
        return Response.ok(service.list()).build();
    }

    @GET
    @Path("/invoke/{pipeline}")
    public Response scan(@PathParam("pipeline") String pipeline) {
        try {
            service.process(pipeline);
            return Response.ok().build();

        } catch (Exception exception) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).encoding(exception.getMessage()).build();
        }
    }

}
