package com.albertsons.edis.springboot.service;

import com.albertsons.edis.ServiceException;
import com.albertsons.edis.JmsMessagePublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.destination.JndiDestinationResolver;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessagePublishServiceImpl implements JmsMessagePublishService {
    static Logger logger = LoggerFactory.getLogger(MessagePublishServiceImpl.class);

    private JndiTemplate jndiTemplate;
    private JndiDestinationResolver destinationResolver;
    private Map<String, JmsTemplate> jmsTemplateCache = new ConcurrentHashMap<>();


    public MessagePublishServiceImpl(JndiTemplate jndiTemplate) {
        this.jndiTemplate = jndiTemplate;
        this.destinationResolver = new JndiDestinationResolver();
        destinationResolver.setFallbackToDynamicDestination(true);
        destinationResolver.setJndiTemplate(jndiTemplate);

    }

    public void publish(String factoryJndiName, String destination, String message) throws ServiceException {
        if(!jmsTemplateCache.containsKey(destination)) {
            JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
            factoryBean.setJndiTemplate(jndiTemplate);
            factoryBean.setJndiName(factoryJndiName);
            factoryBean.setCache(true);
            factoryBean.setProxyInterface(ConnectionFactory.class);
            ConnectionFactory connectionFactory = (ConnectionFactory) factoryBean.getObject();

            JmsTemplate template = new JmsTemplate(connectionFactory);
            template.setDestinationResolver(destinationResolver);
            template.setDefaultDestinationName(destination);

            jmsTemplateCache.put(destination, template);

        }

        jmsTemplateCache.get(destination).send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
        });

    }
}
