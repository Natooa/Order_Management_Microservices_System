package kz.natooa.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.natooa.events.OrderCreatedEvent;
import kz.natooa.order.Orders;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderToOutboxMapper {

    private final ObjectMapper objectMapper;

    public OrderToOutboxMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public Outbox map(OrderCreatedEvent event){
        return Outbox.builder()
                .eventId(event.getEventId().toString())
                .eventType(EventType.ORDER_CREATED_EVENT)
                .aggregateId(event.getOrderId())
                .payload(objectMapper.writeValueAsString(event))
                .status(OutboxStatus.PENDING)
                .createdAt(java.time.Instant.now())
                .build();
    }
}
