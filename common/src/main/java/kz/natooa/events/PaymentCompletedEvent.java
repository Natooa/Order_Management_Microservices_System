package kz.natooa.events;

import kz.natooa.payment.enums.TransactionStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCompletedEvent extends DomainEvent{
    String orderId;
    String transactionId;
    TransactionStatus status;

    public PaymentCompletedEvent(String orderId, String transactionId, TransactionStatus status) {
        this.orderId = orderId;
        this.transactionId = transactionId;
        this.status = status;
    }
}
