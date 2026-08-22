package com.rahul.ecommerce.consumer.service;

import com.rahul.ecommerce.consumer.model.DailySalesSummary;
import com.rahul.ecommerce.consumer.model.OrderEventRow;
import com.rahul.ecommerce.consumer.model.ProductSalesSummary;
import com.rahul.ecommerce.consumer.model.RegionSalesSummary;
import com.rahul.ecommerce.consumer.repository.AnalyticsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsQueryServiceTest {

    @Mock
    private AnalyticsRepository analyticsRepository;

    @InjectMocks
    private AnalyticsQueryService analyticsQueryService;

    @Test
    void getDashboardSnapshot_shouldAggregateAllSummaries() {
        when(analyticsRepository.findTopProducts()).thenReturn(List.of(
                new ProductSalesSummary("Wireless Mouse", "Accessories", 10L, 20L, BigDecimal.valueOf(15000))
        ));
        when(analyticsRepository.findRegionalPerformance()).thenReturn(List.of(
                new RegionSalesSummary("North", 10L, BigDecimal.valueOf(15000))
        ));
        when(analyticsRepository.findDailyTrend()).thenReturn(List.of(
                new DailySalesSummary(LocalDate.now(), 10L, 20L, BigDecimal.valueOf(15000))
        ));
        when(analyticsRepository.countOrders()).thenReturn(10L);

        Map<String, Object> snapshot = analyticsQueryService.getDashboardSnapshot();

        assertEquals(10L, snapshot.get("totalOrders"));
        assertEquals(1, ((List<?>) snapshot.get("topProducts")).size());
        assertEquals(1, ((List<?>) snapshot.get("regions")).size());
        assertEquals(1, ((List<?>) snapshot.get("dailyTrend")).size());
    }

    @Test
    void getRecentOrders_shouldReturnOrderList() {
        when(analyticsRepository.findRecentOrders()).thenReturn(List.of(
                new OrderEventRow("ORD-1", "Wireless Mouse", "Accessories", "North", 2, BigDecimal.valueOf(1500), "UPI", "PLACED", Instant.now())
        ));

        List<OrderEventRow> orders = analyticsQueryService.getRecentOrders();
        assertEquals(1, orders.size());
        assertEquals("ORD-1", orders.get(0).orderId());
    }
}
