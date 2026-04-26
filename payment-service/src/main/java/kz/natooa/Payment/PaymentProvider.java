package kz.natooa.Payment;

import kz.natooa.dto.ProviderResponse;
import kz.natooa.enums.PaymentMethod;

import java.math.BigDecimal;
import java.security.Provider;

public interface PaymentProvider {
    PaymentMethod getSupportedPaymentMethod();
    ProviderResponse processPayment(Payment payment);
    ProviderResponse refundPayment(Payment payment, BigDecimal amount);
    boolean isHealthy();
}
