//package kz.natooa.events;
//
//import kz.natooa.order.Orders;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class OrderEventPublisher {
//
//
//    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;
//
//    public OrderEventPublisher(KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    public void publishOrderCreated(Orders order) {
//        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
//                order.getId().toString(),
//                order.getUserId(),
//                order.getTotalPrice(),
//                order.getCurrency());
//
//        kafkaTemplate
//                .send("order-created-events", order.getId().toString(), orderCreatedEvent).whenComplete(
//                        (result, ex) -> {
//                            if (ex != null) {
//                                System.out.println("Error publishing order created event: " + ex.getMessage());
//                            } else {
//                                log.info("Order created event published successfully: {}", result);
//                            }
//                        }
//                );
//
//    }
//
//}
