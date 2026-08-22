package com.rahul.ecommerce.producer.controller;

import com.rahul.ecommerce.producer.model.GenerateOrdersRequest;
import com.rahul.ecommerce.producer.service.OrderEventGeneratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderEventController.class)
class OrderEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderEventGeneratorService generatorService;

    @Test
    void generateOrders_shouldReturnSuccessResponse() throws Exception {
        when(generatorService.publishBatch(10)).thenReturn(10);

        mockMvc.perform(post("/api/orders/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"count\":10}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.count").value(10))
                .andExpect(jsonPath("$.message").value("Order events published successfully"));
    }
}
