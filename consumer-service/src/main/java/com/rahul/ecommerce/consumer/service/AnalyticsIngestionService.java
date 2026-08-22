package com.rahul.ecommerce.consumer.service;

import com.rahul.ecommerce.consumer.model.OrderEvent;
import com.rahul.ecommerce.consumer.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsIngestionService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsIngestionService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public void save(OrderEvent event) {
        analyticsRepository.saveOrderEvent(event);
    }
}
