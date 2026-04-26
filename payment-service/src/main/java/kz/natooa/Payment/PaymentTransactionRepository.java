package kz.natooa.Payment;

import kz.natooa.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    List<PaymentTransaction> findByPayment(Payment payment);
    Optional<PaymentTransaction> findByTransactionId(String transactionId);
}
