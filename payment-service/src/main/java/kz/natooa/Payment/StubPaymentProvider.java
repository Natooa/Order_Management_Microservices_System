package kz.natooa.Payment;

import kz.natooa.dto.ProviderResponse;
import kz.natooa.enums.PaymentMethod;
import kz.natooa.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class StubPaymentProvider implements PaymentProvider {
    @Override
    public PaymentMethod getSupportedPaymentMethod() {
        return PaymentMethod.CREDIT_CARD;
    }

    @Override
    public ProviderResponse processPayment(Payment payment) {

        if (payment.getPaymentMethod() == getSupportedPaymentMethod()) {
            return ProviderResponse.builder()
                    .success(true)
                    .externalTransactionId("stripe_" + UUID.randomUUID())
                    .status(PaymentStatus.COMPLETED)
                    .message("Payment processed successfuly")
                    .build();
        }
        log.info("Payment failed for payment {}", payment.getPaymentId(), e);
        return ProviderResponse.builder()
                .success(false)
                .status(PaymentStatus.FAILED)
                .message(e.getMessage())
                .errorCode("WRONG PAYMENT METHOD")
                .build();
    }

    @Override
    public ProviderResponse refundPayment(Payment payment, BigDecimal amount) {
        return null;
    }

    @Override
    public boolean isHealthy() {
        return false;
    }
}
