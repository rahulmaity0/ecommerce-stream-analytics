package com.rahul.ecommerce.consumer.service;

import com.rahul.ecommerce.consumer.model.DailySalesSummary;
import com.rahul.ecommerce.consumer.model.OrderEventRow;
import com.rahul.ecommerce.consumer.model.ProductSalesSummary;
import com.rahul.ecommerce.consumer.model.RegionSalesSummary;
import com.rahul.ecommerce.consumer.repository.AnalyticsRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsQueryService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsQueryService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public Map<String, Object> getDashboardSnapshot() {
        List<ProductSalesSummary> topProducts = analyticsRepository.findTopProducts();
        List<RegionSalesSummary> regions = analyticsRepository.findRegionalPerformance();
        List<DailySalesSummary> dailyTrend = analyticsRepository.findDailyTrend();
        long totalOrders = analyticsRepository.countOrders();

        return Map.of(
                "totalOrders", totalOrders,
                "topProducts", topProducts,
                "regions", regions,
                "dailyTrend", dailyTrend
        );
    }

    public List<OrderEventRow> getRecentOrders() {
        return analyticsRepository.findRecentOrders();
    }
}
