package kz.natooa.events;

import kz.natooa.Payment.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

    private final PaymentService paymentService;
    private final PayloadToOrderCreatedEvent payloadMapper;

    public OrderEventHandler(PaymentService paymentService, PayloadToOrderCreatedEvent payloadMapper) {
        this.paymentService = paymentService;
        this.payloadMapper = payloadMapper;
    }

    @KafkaListener(topics = "order-created-events")
    public void handleOrderCreatedEvent(String payload) {
        paymentService.processPayment(payloadMapper.payloadToOrderCreatedEvent(payload));
    }
}
