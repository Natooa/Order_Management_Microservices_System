package kz.natooa.events;

import kz.natooa.Payment.PaymentService;
import kz.natooa.exception.PaymentAlreadyProcessedException;
import kz.natooa.processed.ProcessedMessage;
import kz.natooa.processed.ProcessedMessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Slf4j
public class OrderEventHandler {

    private final PaymentService paymentService;
    private final PayloadToOrderCreatedEvent payloadMapper;
    private final DeduplicationService deduplicationService;

    public OrderEventHandler(PaymentService paymentService,
                             PayloadToOrderCreatedEvent payloadMapper,
                             DeduplicationService deduplicationService) {
        this.paymentService = paymentService;
        this.payloadMapper = payloadMapper;
        this.deduplicationService = deduplicationService;
    }

    @KafkaListener(topics = "order-created-events")
    public void handleOrderCreatedEvent(String payload) throws PaymentAlreadyProcessedException {

        OrderCreatedEvent event = payloadMapper.payloadToOrderCreatedEvent(payload);

        if (!deduplicationService.tryMarkAsProcessed(event.getEventId().toString())) {
            log.warn("Duplicate message, skipping. eventId={}", event.getEventId());
            return;
        }

        paymentService.processPayment(event);

        log.info(
                "Payment processed successfully. orderId={}, eventId={}",
                event.getOrderId(),
                event.getEventId()
        );
    }
}
