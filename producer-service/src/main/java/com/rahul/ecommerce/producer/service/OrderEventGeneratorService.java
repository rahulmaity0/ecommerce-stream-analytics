package com.rahul.ecommerce.producer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.ecommerce.producer.model.OrderEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OrderEventGeneratorService {

    private static final List<String> REGIONS = List.of("North", "South", "East", "West");
    private static final List<String> PAYMENTS = List.of("CARD", "UPI", "NET_BANKING", "WALLET");
    private static final List<String> STATUSES = List.of("PLACED", "CONFIRMED", "SHIPPED", "DELIVERED");
    private static final List<ProductDefinition> PRODUCTS = List.of(
            new ProductDefinition("PRD-101", "Wireless Mouse", "Accessories", 799),
            new ProductDefinition("PRD-102", "Mechanical Keyboard", "Accessories", 2499),
            new ProductDefinition("PRD-103", "Gaming Monitor", "Monitors", 14999),
            new ProductDefinition("PRD-104", "USB-C Hub", "Accessories", 1899),
            new ProductDefinition("PRD-105", "Noise Cancelling Headphones", "Audio", 6999),
            new ProductDefinition("PRD-106", "External SSD", "Storage", 5499),
            new ProductDefinition("PRD-107", "Webcam", "Accessories", 3299),
            new ProductDefinition("PRD-108", "Laptop Stand", "Office", 1299)
    );

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topicName;
    private final boolean autoGenerateEnabled;
    private final int autoGenerateBatchSize;

    public OrderEventGeneratorService(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.topic}") String topicName,
            @Value("${app.generator.auto-enabled}") boolean autoGenerateEnabled,
            @Value("${app.generator.auto-batch-size}") int autoGenerateBatchSize) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.topicName = topicName;
        this.autoGenerateEnabled = autoGenerateEnabled;
        this.autoGenerateBatchSize = autoGenerateBatchSize;
    }

    public int publishBatch(int count) {
        for (int i = 0; i < count; i++) {
            OrderEvent event = buildRandomOrder();
            kafkaTemplate.send(topicName, event.orderId(), toJson(event));
        }
        return count;
    }

    @Scheduled(fixedRateString = "${app.generator.auto-interval-ms}")
    public void publishScheduledBatch() {
        if (!autoGenerateEnabled) {
            return;
        }
        publishBatch(autoGenerateBatchSize);
    }

    private OrderEvent buildRandomOrder() {
        ProductDefinition product = PRODUCTS.get(ThreadLocalRandom.current().nextInt(PRODUCTS.size()));
        int quantity = ThreadLocalRandom.current().nextInt(1, 6);
        BigDecimal unitPrice = BigDecimal.valueOf(product.unitPrice());
        BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal discount = gross.multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(0.0, 0.20)))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = gross.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        return new OrderEvent(
                "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(),
                "CUST-" + ThreadLocalRandom.current().nextInt(1000, 9999),
                product.productId(),
                product.productName(),
                product.category(),
                pick(REGIONS),
                quantity,
                unitPrice,
                discount,
                total,
                pick(PAYMENTS),
                pick(STATUSES),
                Instant.now()
        );
    }

    private String toJson(OrderEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize order event", ex);
        }
    }

    private String pick(List<String> values) {
        return values.get(ThreadLocalRandom.current().nextInt(values.size()));
    }

    private record ProductDefinition(String productId, String productName, String category, int unitPrice) {
    }
}
