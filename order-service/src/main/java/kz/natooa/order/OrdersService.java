package kz.natooa.order;

import kz.natooa.orderItems.OrderItemRequestDTO;

import java.util.List;

public interface OrdersService {
    OrderResponse createOrder(String userId, List<OrderItemRequestDTO> orderItems);
    void cancelOrder(String orderId);
    void confirmOrder(String orderId);
    Orders updateOrderStatus(String orderId, Status status);
}
