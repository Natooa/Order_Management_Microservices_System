package kz.natooa.grpc;

import kz.natooa.order.Orders;
import kz.natooa.orderItems.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class InventoryAdapter {

    private final InventoryGrpcClient inventoryGrpcClient;

    public InventoryAdapter(InventoryGrpcClient inventoryGrpcClient) {
        this.inventoryGrpcClient = inventoryGrpcClient;
    }

    public void reserve(Orders order) {
        for (OrderItem item : order.getOrderItems()) {
            inventoryGrpcClient.reserveInventory(
                    item.getProductId(),
                    item.getQuantity(),
                    order.getId().toString()
            );
        }
    }

    public void cancelReservation(String orderId) {
        inventoryGrpcClient.cancelReservation(orderId);
    }

    public void confirmReservation(String orderId) {
        inventoryGrpcClient.confirmReservation(orderId);
    }
}
