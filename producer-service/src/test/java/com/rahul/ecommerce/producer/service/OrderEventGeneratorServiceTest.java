package com.rahul.ecommerce.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rahul.ecommerce.producer.model.OrderEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventGeneratorServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Captor
    private ArgumentCaptor<String> topicCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<String> payloadCaptor;

    private ObjectMapper objectMapper;
    private OrderEventGeneratorService service;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        service = new OrderEventGeneratorService(
                kafkaTemplate,
                objectMapper,
                "order-events",
                true,
                5
        );
    }

    @Test
    void publishBatch_shouldSendExactCountOfEventsToKafka() throws Exception {
        int count = 5;
        int published = service.publishBatch(count);

        assertEquals(5, published);
        verify(kafkaTemplate, times(5)).send(topicCaptor.capture(), keyCaptor.capture(), payloadCaptor.capture());

        for (int i = 0; i < count; i++) {
            assertEquals("order-events", topicCaptor.getAllValues().get(i));
            assertNotNull(keyCaptor.getAllValues().get(i));
            assertTrue(keyCaptor.getAllValues().get(i).startsWith("ORD-"));

            String payload = payloadCaptor.getAllValues().get(i);
            OrderEvent event = objectMapper.readValue(payload, OrderEvent.class);

            assertNotNull(event.orderId());
            assertNotNull(event.customerId());
            assertNotNull(event.productId());
            assertNotNull(event.productName());
            assertNotNull(event.category());
            assertNotNull(event.region());
            assertTrue(event.quantity() >= 1 && event.quantity() <= 5);
            assertNotNull(event.unitPrice());
            assertNotNull(event.discountAmount());
            assertNotNull(event.totalAmount());
            assertNotNull(event.paymentMethod());
            assertNotNull(event.orderStatus());
            assertNotNull(event.eventTime());
        }
    }

    @Test
    void publishScheduledBatch_shouldPublishWhenEnabled() {
        service.publishScheduledBatch();
        verify(kafkaTemplate, times(5)).send(anyString(), anyString(), anyString());
    }

    @Test
    void publishScheduledBatch_shouldNotPublishWhenDisabled() {
        OrderEventGeneratorService disabledService = new OrderEventGeneratorService(
                kafkaTemplate,
                objectMapper,
                "order-events",
                false,
                5
        );
        disabledService.publishScheduledBatch();
        verifyNoInteractions(kafkaTemplate);
    }
}
