package kz.natooa.Payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, Long> {
    List<PaymentRefund> findByPayment(Payment payment);
    Optional<PaymentRefund> findByRefundId(String refundId);
}
