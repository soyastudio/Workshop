package soya.framework.tools.workbench.resource;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Component;
import soya.framework.tools.markdown.TodoList;
import soya.framework.tools.markdown.mermaid.FlowChart;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Component
@Path("/markkdown")
@Api(value = "Markdown Service", hidden = true)
public class MarkdownResource {

    @POST
    @Path("/todo")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response todoList(String markdown) {
        return Response.status(200).entity(TodoList.scan(markdown)).build();
    }

    @POST
    @Path("/mermaid/flow-chart")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response flowChart(String markdown) {
        return Response.status(200).entity(FlowChart.scan(markdown)).build();
    }

    @POST
    @Path("/mermaid/norm")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response norm(String markdown) {
        FlowChart chart = FlowChart.scan(markdown).get(0);
        return Response.status(200).entity(chart.toString()).build();
    }

}
