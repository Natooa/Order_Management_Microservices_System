package kz.natooa.repository;

import kz.natooa.dto.ShipmentStatus;
import kz.natooa.entity.Shipments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentsRepository extends JpaRepository<Shipments, Long>{
    Optional<Shipments> findByShipmentId(String paymentId);
    Optional<Shipments> findByOrderId(String orderId);
    List<Shipments> findByUserIdAndShipmentStatus(String userId, ShipmentStatus status);

    @Query("SELECT s FROM Shipments s WHERE s.status = :status AND s.estimatedDeliveryDate < :cutoff")
    List<Shipments> findStuckShipments(@Param("status") ShipmentStatus status, @Param("cutoff") Instant cutoff);
}
