package kz.natooa.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentFailedEvent extends DomainEvent{
    String orderId;
    String reason;

    public PaymentFailedEvent(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }
}
