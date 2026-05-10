package kz.natooa.Payment;

import kz.natooa.dto.ProviderResponse;
import kz.natooa.payment.enums.PaymentMethod;

import java.math.BigDecimal;

public interface PaymentProvider {
    PaymentMethod getSupportedPaymentMethod();
    ProviderResponse processPayment(Payment payment);
    ProviderResponse refundPayment(Payment payment, BigDecimal amount);
    boolean isHealthy();
}
