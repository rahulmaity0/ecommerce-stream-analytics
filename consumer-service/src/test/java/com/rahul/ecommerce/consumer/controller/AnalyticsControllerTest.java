package com.rahul.ecommerce.consumer.controller;

import com.rahul.ecommerce.consumer.model.OrderEventRow;
import com.rahul.ecommerce.consumer.service.AnalyticsQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsQueryService analyticsQueryService;

    @Test
    void dashboard_shouldReturnSnapshot() throws Exception {
        when(analyticsQueryService.getDashboardSnapshot()).thenReturn(Map.of(
                "totalOrders", 42L,
                "topProducts", List.of(),
                "regions", List.of(),
                "dailyTrend", List.of()
        ));

        mockMvc.perform(get("/api/analytics/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(42));
    }

    @Test
    void recentOrders_shouldReturnOrderList() throws Exception {
        when(analyticsQueryService.getRecentOrders()).thenReturn(List.of(
                new OrderEventRow("ORD-99", "Gaming Monitor", "Monitors", "West", 1, BigDecimal.valueOf(14999), "CARD", "DELIVERED", Instant.now())
        ));

        mockMvc.perform(get("/api/analytics/orders/recent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("ORD-99"))
                .andExpect(jsonPath("$[0].productName").value("Gaming Monitor"));
    }
}
