package com.rahul.ecommerce.consumer.model;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderEventRow(
        String orderId,
        String productName,
        String category,
        String region,
        int quantity,
        BigDecimal totalAmount,
        String paymentMethod,
        String orderStatus,
        Instant eventTime
) {
}
