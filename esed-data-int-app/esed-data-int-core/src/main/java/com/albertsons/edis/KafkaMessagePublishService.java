package com.albertsons.edis;

public interface KafkaMessagePublishService {
    void publish(String topicName, String message) throws ServiceException;
}
