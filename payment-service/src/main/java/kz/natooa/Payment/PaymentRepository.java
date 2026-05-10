package kz.natooa.Payment;

import kz.natooa.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentId(String paymentId);
    Optional<Payment> findByOrderId(String orderId);
    List<Payment> findByUserIdAndPaymentStatus(String userId, PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = :status AND p.createdAt < :cutoff")
    List<Payment> findStuckPayments(@Param("status") PaymentStatus status, @Param("cutoff") Instant cutoff);
}
