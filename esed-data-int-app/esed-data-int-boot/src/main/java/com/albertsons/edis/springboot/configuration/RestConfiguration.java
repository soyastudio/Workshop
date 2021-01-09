package com.albertsons.edis.springboot.configuration;

import com.albertsons.edis.springboot.resource.PipelineResource;
import com.albertsons.edis.springboot.resource.ServiceDispatchResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.ApplicationPath;

@Configuration
@ApplicationPath("/edis")
public class RestConfiguration extends ResourceConfig {

    public RestConfiguration() {
        register(GsonMessageBodyHandler.class);

        register(PipelineResource.class);
        register(ServiceDispatchResource.class);
    }
}
