package com.rahul.ecommerce.consumer.model;

import java.math.BigDecimal;

public record RegionSalesSummary(
        String region,
        long totalOrders,
        BigDecimal revenue
) {
}
