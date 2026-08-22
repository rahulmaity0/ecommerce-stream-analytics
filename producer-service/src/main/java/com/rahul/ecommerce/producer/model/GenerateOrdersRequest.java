package com.rahul.ecommerce.producer.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GenerateOrdersRequest(
        @Min(1)
        @Max(500)
        int count
) {
}
