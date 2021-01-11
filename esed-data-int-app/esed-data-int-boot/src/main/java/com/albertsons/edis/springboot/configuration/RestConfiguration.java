package com.albertsons.edis.springboot.configuration;

import com.albertsons.edis.springboot.resource.PipelineResource;
import com.albertsons.edis.springboot.resource.ServiceDispatchResource;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Swagger;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/api")
public class RestConfiguration extends ResourceConfig {
    private static String PACKAGE_NAME = PipelineResource.class.getPackage().getName();

    public RestConfiguration() {
        register(GsonMessageBodyHandler.class);
        packages(PACKAGE_NAME);
        swaggerConfig();
    }

    private Swagger swaggerConfig() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig swaggerConfigBean = new BeanConfig();
        swaggerConfigBean.setConfigId("EDIS");
        swaggerConfigBean.setTitle("Enterprise Data Integration Server");
        //swaggerConfigBean.setVersion("v1");
        swaggerConfigBean.setContact("wenqun.soya@gmail.com");
        swaggerConfigBean.setSchemes(new String[]{"http"});
        swaggerConfigBean.setBasePath("/api");
        swaggerConfigBean.setResourcePackage(PACKAGE_NAME);
        swaggerConfigBean.setPrettyPrint(true);
        swaggerConfigBean.setScan(true);

        return swaggerConfigBean.getSwagger();
    }
}
