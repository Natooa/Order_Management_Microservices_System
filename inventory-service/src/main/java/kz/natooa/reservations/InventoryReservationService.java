package kz.natooa.reservations;

import kz.natooa.inventory.ReserveInventoryResponse;

import java.util.UUID;

public interface InventoryReservationService {
    ReserveInventoryResponse reserveInventory(String productId, Integer quantity, UUID orderId);
    void cancelReservation(UUID orderId);
    void confirmReservation(UUID orderId);
}
