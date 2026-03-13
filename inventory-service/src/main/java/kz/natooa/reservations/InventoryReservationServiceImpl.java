package kz.natooa.reservations;

import kz.natooa.inventory.InventoryService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class InventoryReservationServiceImpl implements InventoryReservationService{
    private final InventoryReservationRepository inventoryReservationRepository;
    private final InventoryService inventoryService;

    public InventoryReservationServiceImpl(InventoryReservationRepository inventoryReservationRepository, InventoryService inventoryService) {
        this.inventoryReservationRepository = inventoryReservationRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    @Override
    public void reserveInventory(String productId, Integer quantity, UUID orderId) {
        if(productId == null || quantity == null || orderId == null){
            throw new IllegalArgumentException("Product ID, quantity, and order ID must not be null");
        }
        InventoryReservation reservation = InventoryReservation.builder()
                .productId(productId)
                .orderId(orderId)
                .quantity(quantity)
                .status(ReservationStatus.RESERVED)
                .build();
        try {
            inventoryReservationRepository.save(reservation);
        } catch (DataIntegrityViolationException e){
            return;
        }

        inventoryService.increaseReserved(productId, quantity);
    }

    @Transactional
    @Override
    public void cancelReservation(UUID orderId) {
        List<InventoryReservation> reservations = inventoryReservationRepository.findAllByOrderIdForUpdate(orderId);
        if(reservations.isEmpty()){
            throw new IllegalArgumentException("No reservations found for this order");
        }
        if(reservations.stream().anyMatch(reservation -> reservation.getStatus() == ReservationStatus.CONFIRMED)){
            throw new IllegalArgumentException("Cannot cancel confirmed reservations");
        }
        if(reservations.stream().anyMatch(r -> r.getStatus() == ReservationStatus.CANCELLED)) {
            throw new IllegalArgumentException("Reservation already cancelled");
        }
        reservations.forEach(reservation -> {
            inventoryService.decreaseReserved(reservation.getProductId(), reservation.getQuantity());
            reservation.setStatus(ReservationStatus.CANCELLED);
        });
        inventoryReservationRepository.saveAll(reservations);
    }

    @Transactional
    @Override
    public void confirmReservation(UUID orderId) {
        List<InventoryReservation> reservations = inventoryReservationRepository.findAllByOrderIdForUpdate(orderId);
        if(reservations.isEmpty()){
            throw new IllegalArgumentException("No reservations found for this order");
        }
        if(reservations.stream().anyMatch(reservation -> reservation.getStatus() == ReservationStatus.CONFIRMED)){
            throw new IllegalArgumentException("Order already confirmed");
        }
        if(reservations.stream().anyMatch(r -> r.getStatus() == ReservationStatus.CANCELLED)) {
            throw new IllegalArgumentException("Reservation already cancelled");
        }

        reservations.forEach(reservation -> {
            inventoryService.commitReserved(reservation.getProductId(), reservation.getQuantity());
            reservation.setStatus(ReservationStatus.CONFIRMED);
        });
        inventoryReservationRepository.saveAll(reservations);
    }
}
