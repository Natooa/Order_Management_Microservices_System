package kz.natooa.order;

import kz.natooa.events.PaymentCompletedEvent;
import kz.natooa.events.PaymentFailedEvent;
import kz.natooa.orderItems.OrderItemRequestDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface OrdersService {
    OrderResponse createOrder(String userId, OrderRequestDTO requestDTO) throws ExecutionException, InterruptedException;
    void cancelOrder(PaymentFailedEvent event);
    void confirmOrder(PaymentCompletedEvent event);
    Orders updateOrderStatus(String orderId, Status status);
}
