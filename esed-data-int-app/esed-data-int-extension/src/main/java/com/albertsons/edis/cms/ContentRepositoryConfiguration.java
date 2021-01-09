package com.albertsons.edis.cms;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class ContentRepositoryConfiguration {

    @Bean
    ContentRepositoryService contentRepositoryService() {
        File serverHome = new File(System.getProperty("edis.server.home"));
        File repositoryHome = new File(serverHome, "repository");
        if(!repositoryHome.exists()) {
            repositoryHome.mkdirs();
        }

        return new ContentRepositoryService(repositoryHome);
    }
}
