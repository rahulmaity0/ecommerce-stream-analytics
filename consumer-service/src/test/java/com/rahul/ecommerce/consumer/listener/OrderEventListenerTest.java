package com.rahul.ecommerce.consumer.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rahul.ecommerce.consumer.model.OrderEvent;
import com.rahul.ecommerce.consumer.service.AnalyticsIngestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderEventListenerTest {

    @Mock
    private AnalyticsIngestionService ingestionService;

    @Captor
    private ArgumentCaptor<OrderEvent> eventCaptor;

    private ObjectMapper objectMapper;
    private OrderEventListener listener;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        listener = new OrderEventListener(objectMapper, ingestionService);
    }

    @Test
    void consume_shouldDeserializeAndSaveEvent() throws Exception {
        OrderEvent event = new OrderEvent(
                "ORD-TEST-1",
                "CUST-101",
                "PRD-101",
                "Wireless Mouse",
                "Accessories",
                "North",
                2,
                BigDecimal.valueOf(799),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(1548),
                "UPI",
                "PLACED",
                Instant.now()
        );

        String json = objectMapper.writeValueAsString(event);
        listener.consume(json);

        verify(ingestionService).save(eventCaptor.capture());
        OrderEvent saved = eventCaptor.getValue();
        assertEquals("ORD-TEST-1", saved.orderId());
        assertEquals("CUST-101", saved.customerId());
        assertEquals("Wireless Mouse", saved.productName());
    }
}
