package kz.natooa.events;

import kz.natooa.payment.enums.AvailableCurrency;
import kz.natooa.payment.enums.PaymentMethod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class OrderCreatedEvent extends DomainEvent{
    String orderId;
    String userId;
    BigDecimal totalPrice;
    AvailableCurrency currency;

    public OrderCreatedEvent(String orderId, String userId, BigDecimal totalPrice, AvailableCurrency currency) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.currency = currency;
    }
}
