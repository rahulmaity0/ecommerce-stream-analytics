package com.rahul.ecommerce.consumer.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DailySalesSummary(
        LocalDate orderDate,
        long totalOrders,
        long totalUnitsSold,
        BigDecimal revenue
) {
}
