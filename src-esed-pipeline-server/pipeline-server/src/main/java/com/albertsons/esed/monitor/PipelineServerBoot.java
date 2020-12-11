package com.albertsons.esed.monitor;

import com.albertsons.esed.monitor.server.PipelineServer;
import com.albertsons.esed.monitor.server.ServiceEventListener;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class PipelineServerBoot extends PipelineServer {

    public static void main(String[] args) {
        new PipelineServerBoot();
        SpringApplication.run(PipelineServerBoot.class, args);
    }

    private ApplicationContext applicationContext;

    public String name() {
        return "esed-pipeline-server";
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        return applicationContext.getBean(serviceType);
    }

    @EventListener(classes = {ApplicationReadyEvent.class})
    public void onApplicationEvent(ApplicationReadyEvent event) {
        this.applicationContext = event.getApplicationContext();
        applicationContext.getBeansOfType(ServiceEventListener.class).values().forEach(e -> {
            register(e);
        });
    }
}
