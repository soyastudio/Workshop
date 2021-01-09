package com.albertsons.edis;

public interface JmsMessagePublishService {
    void publish(String factoryJndiName, String destination, String message) throws ServiceException;
}
