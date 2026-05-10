package kz.natooa.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class PayloadToOrderCreatedEvent {

    private final ObjectMapper objectMapper;

    public PayloadToOrderCreatedEvent(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OrderCreatedEvent payloadToOrderCreatedEvent(String payload) {
        try {
            return objectMapper.readValue(
                    payload,
                    OrderCreatedEvent.class
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to deserialize OrderCreatedEvent",
                    e
            );
        }
    }
}