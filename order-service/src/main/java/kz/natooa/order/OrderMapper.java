package kz.natooa.order;

import kz.natooa.orderItems.OrderItem;
import kz.natooa.orderItems.OrderItemResponse;

public class OrderMapper {

    public static OrderResponse toResponse(Orders order) {
        return new OrderResponse(
                order.getId().toString(),
                order.getTotalPrice(),
                order.getOrderItems()
                        .stream()
                        .map(OrderMapper::toItemResponse)
                        .toList(),
                order.getStatus()
        );
    }

    private static OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getProductId(),
                item.getQuantity(),
                item.getPriceAtPurchase()
        );
    }
}
