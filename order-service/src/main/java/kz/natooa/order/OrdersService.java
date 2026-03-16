package kz.natooa.order;

import kz.natooa.orderItems.OrderItem;
import kz.natooa.orderItems.OrderItemRequestDTO;

import java.math.BigDecimal;
import java.util.List;

public interface OrdersService {
    Orders createOrder(String userId, List<OrderItemRequestDTO> orderItems);
    Orders cancelOrder(String orderId);
    Orders updateOrderStatus(String orderId, Status status);
}
