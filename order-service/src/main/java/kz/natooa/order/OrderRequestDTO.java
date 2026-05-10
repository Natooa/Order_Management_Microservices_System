package kz.natooa.order;

import kz.natooa.orderItems.OrderItemRequestDTO;
import kz.natooa.payment.enums.AvailableCurrency;

import java.util.List;

public record OrderRequestDTO (
        List<OrderItemRequestDTO> items,
        AvailableCurrency currency
){
}
