package kz.natooa;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.natooa.events.OrderCreatedEvent;
import kz.natooa.events.PayloadToOrderCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PayloadToOrderCreatedEventTest {

    private PayloadToOrderCreatedEvent mapper;

    @BeforeEach
    void setUp() {
        mapper = new PayloadToOrderCreatedEvent(new ObjectMapper());
    }

    @Test
    void shouldMapPayloadToOrderCreatedEvent() {

        String payload = """
                {
                  "orderId": "order-123",
                  "userId": "user-999",
                  "totalPrice": 1500.50,
                  "currency": "USD"
                }
                """;

        OrderCreatedEvent event =
                mapper.payloadToOrderCreatedEvent(payload);

        assertNotNull(event);
        assertEquals("order-123", event.getOrderId());
        assertEquals("user-999", event.getUserId());
        assertEquals(
                new BigDecimal("1500.50"),
                event.getTotalPrice()
        );
    }

    @Test
    void shouldThrowExceptionForInvalidPayload() {

        String payload = "invalid json";

        assertThrows(
                RuntimeException.class,
                () -> mapper.payloadToOrderCreatedEvent(payload)
        );
    }
}