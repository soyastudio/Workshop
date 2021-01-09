package com.albertsons.edis.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.jms.annotation.EnableJms;

@org.springframework.boot.autoconfigure.SpringBootApplication
@ComponentScan({"com.albertsons.edis"})
@EnableJms
public class SpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);
    }

    @EventListener(classes = {ApplicationReadyEvent.class})
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        String[] names = applicationContext.getBeanDefinitionNames();
        for(String name: names) {
            System.out.println("-------- " + name + ": " + applicationContext.getBean(name).getClass());
        }
    }

}
