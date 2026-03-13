package kz.natooa.reservations;

import java.util.UUID;

public interface InventoryReservationService {
    void reserveInventory(String productId, Integer quantity, UUID orderId);
    void cancelReservation(UUID orderId);
    void confirmReservation(UUID orderId);
}
