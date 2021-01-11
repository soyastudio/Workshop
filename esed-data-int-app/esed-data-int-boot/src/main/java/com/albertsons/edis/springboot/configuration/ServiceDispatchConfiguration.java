package com.albertsons.edis.springboot.configuration;

import com.albertsons.edis.Delegate;
import com.albertsons.edis.ServiceDelegate;
import com.albertsons.edis.ServiceDispatcher;
import com.albertsons.edis.springboot.service.ServiceDelegateDispatcher;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class ServiceDispatchConfiguration {
    @Bean
    ServiceDispatcher serviceDispatcher() {
        return new ServiceDelegateDispatcher();
    }

    @EventListener(classes = {ApplicationReadyEvent.class})
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        ServiceDispatcher serviceDispatcher = applicationContext.getBean(ServiceDispatcher.class);


        String[] names = applicationContext.getBeanDefinitionNames();
        for(String name: names) {
            Object bean = applicationContext.getBean(name);
            if(bean.getClass().getAnnotation(Delegate.class) != null) {
                System.out.println("-------- " + name + ": " + bean.getClass());
                serviceDispatcher.register(bean);
            }

            if(bean instanceof ServiceDelegate) {

                serviceDispatcher.register(bean);
            }

            //
        }
    }
}
