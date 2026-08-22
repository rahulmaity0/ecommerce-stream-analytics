package com.rahul.ecommerce.producer.controller;

import com.rahul.ecommerce.producer.model.GenerateOrdersRequest;
import com.rahul.ecommerce.producer.service.OrderEventGeneratorService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderEventController {

    private final OrderEventGeneratorService generatorService;

    public OrderEventController(OrderEventGeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @PostMapping("/generate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Object> generateOrders(@Valid @RequestBody GenerateOrdersRequest request) {
        int generatedCount = generatorService.publishBatch(request.count());
        return Map.of(
                "message", "Order events published successfully",
                "count", generatedCount
        );
    }
}
