package com.albertsons.esed.monitor.resource;

import com.albertsons.esed.monitor.server.PipelineService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/scan")
@Api(value = "API Scan Service")
public class ApiMonitorResource {

    @Autowired
    private PipelineService service;

    @GET
    @Path("/{pipeline}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response scan(@PathParam("pipeline") String pipeline) {
        return Response.status(200).build();
    }

    @GET
    @Path("/full/{pipeline}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response fllScan(@PathParam("pipeline") String pipeline) {
        return Response.status(200).build();
    }

}
