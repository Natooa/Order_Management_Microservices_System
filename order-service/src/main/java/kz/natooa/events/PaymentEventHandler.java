package kz.natooa.events;

import kz.natooa.order.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventHandler {

    @Qualifier("OrderServiceImpl")
    private final OrdersService ordersService;

    public PaymentEventHandler(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @KafkaListener(topics = "payment-completed-events")
    public void handlePaymentCompletedEvent(PaymentCompletedEvent event) {
        ordersService.confirmOrder(event);
    }

    @KafkaListener(topics = "payment-failed-events")
    public void handlePaymentFailedEvent(PaymentFailedEvent event) {
        ordersService.cancelOrder(event);
    }
}