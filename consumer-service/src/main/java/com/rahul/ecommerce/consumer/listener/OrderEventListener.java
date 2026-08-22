package com.rahul.ecommerce.consumer.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.ecommerce.consumer.model.OrderEvent;
import com.rahul.ecommerce.consumer.service.AnalyticsIngestionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final ObjectMapper objectMapper;
    private final AnalyticsIngestionService ingestionService;

    public OrderEventListener(ObjectMapper objectMapper, AnalyticsIngestionService ingestionService) {
        this.objectMapper = objectMapper;
        this.ingestionService = ingestionService;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String payload) {
        try {
            OrderEvent event = objectMapper.readValue(payload, OrderEvent.class);
            ingestionService.save(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Unable to parse order event payload", ex);
        }
    }
}
