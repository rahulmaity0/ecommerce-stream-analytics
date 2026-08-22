package com.rahul.ecommerce.consumer.model;

import java.math.BigDecimal;

public record ProductSalesSummary(
        String productName,
        String category,
        long totalOrders,
        long totalUnitsSold,
        BigDecimal revenue
) {
}
