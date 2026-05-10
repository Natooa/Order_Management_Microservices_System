package kz.natooa.orderItems;

import java.math.BigDecimal;

public record OrderItemResponse (
        String productId,
        int quantity,
        BigDecimal priceAtPurchase
){
}
