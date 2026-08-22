package com.rahul.ecommerce.producer.model;

import java.math.BigDecimal;
import java.time.Instant;

public record OrderEvent(
        String orderId,
        String customerId,
        String productId,
        String productName,
        String category,
        String region,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        String paymentMethod,
        String orderStatus,
        Instant eventTime
) {
}
