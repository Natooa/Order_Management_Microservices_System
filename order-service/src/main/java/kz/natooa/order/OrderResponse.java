package kz.natooa.order;

import kz.natooa.orderItems.OrderItemResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse (
        String orderId,
        BigDecimal totalPrice,
        List<OrderItemResponse> items,
        Status status){
}
