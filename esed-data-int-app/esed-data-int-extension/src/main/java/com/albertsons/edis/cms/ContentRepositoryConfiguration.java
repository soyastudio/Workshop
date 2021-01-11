package com.albertsons.edis.cms;

import com.albertsons.edis.ServiceDelegate;
import com.albertsons.edis.ServiceDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class ContentRepositoryConfiguration {
    @Autowired
    ServiceDispatcher dispatcher;

    @Bean
    ContentRepositoryService contentRepositoryService() {
        File serverHome = new File(System.getProperty("edis.server.home"));
        File repositoryHome = new File(serverHome, "repository");
        if(!repositoryHome.exists()) {
            repositoryHome.mkdirs();
        }

        return new ContentRepositoryService(repositoryHome);
    }

    @Bean
    ContentRepositoryServiceDelegate contentRepositoryServiceDelegate(ContentRepositoryService contentRepositoryService) {

        return new ContentRepositoryServiceDelegate() {

            @Override
            public String getContentsAsString(String path) {
                return null;
            }
        };
    }

    static interface ContentRepositoryServiceDelegate extends ServiceDelegate {
        String getContentsAsString(String path);
    }

}
