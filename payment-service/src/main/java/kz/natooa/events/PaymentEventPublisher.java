package kz.natooa.events;

import kz.natooa.Payment.Payment;
import kz.natooa.Payment.PaymentTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class PaymentEventPublisher {


    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public PaymentEventPublisher(KafkaTemplate<String, DomainEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, DomainEvent>> publishPaymentCompleted(PaymentTransaction transaction) {
        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(
                transaction.getPayment().getOrderId(),
                transaction.getTransactionId(),
                transaction.getStatus()
        );

        return kafkaTemplate
                .send("payment-completed-events", paymentCompletedEvent.getOrderId(), paymentCompletedEvent).whenComplete(
                        (result, ex) -> {
                            if (ex != null) {
                                log.error("Error publishing payment completed event, orderId={}, error={}", paymentCompletedEvent.getOrderId(), ex.getMessage());
                            } else{
                                log.info("Payment completed event published successfully, orderId={}, offset={}", paymentCompletedEvent.getOrderId(), result.getRecordMetadata().offset());
                            }
                        }
                );

    }

    public CompletableFuture<SendResult<String, DomainEvent>> publishPaymentFailed(Payment payment) {
        PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                payment.getOrderId(),
                payment.getFailureReason()
        );

        return kafkaTemplate
                .send("payment-failed-events", paymentFailedEvent.getOrderId(), paymentFailedEvent).whenComplete(
                        (result, ex) -> {
                            if (ex != null) {
                                log.error("Error publishing payment failed event, orderId={}, error={} ", paymentFailedEvent.getOrderId(), ex.getMessage());
                            } else{
                                log.info("Payment failed event published successfully, orderId={}, offset={}", paymentFailedEvent.getOrderId(), result.getRecordMetadata().offset());
                            }
                        }
                );

    }

}
