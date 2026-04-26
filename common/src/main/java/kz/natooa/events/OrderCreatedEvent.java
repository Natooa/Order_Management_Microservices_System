package kz.natooa.events;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderCreatedEvent extends DomainEvent{
    String orderId;
    String userId;
    BigDecimal totalPrice;
}
