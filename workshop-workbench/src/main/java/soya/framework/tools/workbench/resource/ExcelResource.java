package soya.framework.tools.workbench.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;
import soya.framework.tools.poi.XlsxUtils;
import soya.framework.tools.xmlbeans.XmlBeansUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;

@Component
@Path("/excel")
@Api(value = "Excel Service", hidden = false)
public class ExcelResource {

    @POST
    @Path("/json/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Excel to json through file upload")
    public Response uploadFile(@HeaderParam("worksheet") String worksheet,
                                @FormDataParam("file") InputStream fileInputStream,
                               @FormDataParam("file") FormDataContentDisposition fileMetaData) {
        try {
            JsonArray jsonArray = XlsxUtils.fromWorksheet(fileInputStream, worksheet);
            return Response.ok(jsonArray).build();
        } catch (Exception e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    @POST
    @Path("/json/url")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("Excel to json through local file")
    public Response excelToJson(@HeaderParam("url") String url, @HeaderParam("worksheet") String worksheet) {
        try {
            File file = new File(url);
            JsonElement result = XlsxUtils.fromWorksheet(file, worksheet);

            return Response.status(200).entity(result).build();
        } catch (Exception e) {
            return Response.status(500).build();
        }
    }

    @POST
    @Path("/xsdToXml")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Response xsdToXml(String xsd) throws MalformedURLException {
        return Response.status(200).entity(XmlBeansUtils.xsdToXml(xsd)).build();
    }

    @POST
    @Path("/xmlToJson")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response xmlToJson(String xml) {
        JSONObject xmlJSONObj = XML.toJSONObject(xml);
        String json = xmlJSONObj.toString(4);
        return Response.status(200).entity(json).build();
    }
}
