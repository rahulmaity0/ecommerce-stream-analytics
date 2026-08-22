package com.rahul.ecommerce.consumer.controller;

import com.rahul.ecommerce.consumer.service.AnalyticsQueryService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsQueryService analyticsQueryService;

    public AnalyticsController(AnalyticsQueryService analyticsQueryService) {
        this.analyticsQueryService = analyticsQueryService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return analyticsQueryService.getDashboardSnapshot();
    }

    @GetMapping("/orders/recent")
    public List<?> recentOrders() {
        return analyticsQueryService.getRecentOrders();
    }
}
