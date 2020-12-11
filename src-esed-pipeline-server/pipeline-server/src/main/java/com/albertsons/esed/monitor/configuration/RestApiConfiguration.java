package com.albertsons.esed.monitor.configuration;

import com.albertsons.esed.monitor.resource.ApiMonitorResource;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Swagger;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/api")
public class RestApiConfiguration extends ResourceConfig {
    public RestApiConfiguration() {
        register(ApiMonitorResource.class);
        swaggerConfig();
    }

    private Swagger swaggerConfig() {
        this.register(ApiListingResource.class);
        this.register(SwaggerSerializers.class);

        BeanConfig swaggerConfigBean = new BeanConfig();
        swaggerConfigBean.setConfigId("esed-pipeline-server");
        swaggerConfigBean.setTitle("ESED Pipeline Server");
        swaggerConfigBean.setSchemes(new String[]{"http"});
        swaggerConfigBean.setBasePath("/api");
        swaggerConfigBean.setResourcePackage("com.albertsons.esed.monitor.resource");
        swaggerConfigBean.setPrettyPrint(true);
        swaggerConfigBean.setScan(true);

        return swaggerConfigBean.getSwagger();
    }
}
