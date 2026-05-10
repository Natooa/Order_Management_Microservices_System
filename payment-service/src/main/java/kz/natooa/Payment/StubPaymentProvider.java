package kz.natooa.Payment;

import kz.natooa.dto.ProviderResponse;
import kz.natooa.payment.enums.PaymentMethod;
import kz.natooa.enums.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
@Slf4j
public class StubPaymentProvider implements PaymentProvider {
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final PaymentRefundRepository paymentRefundRepository;

    public StubPaymentProvider(PaymentRepository paymentRepository, PaymentTransactionRepository paymentTransactionRepository, PaymentRefundRepository paymentRefundRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.paymentRefundRepository = paymentRefundRepository;
    }

    @Override
    public PaymentMethod getSupportedPaymentMethod() {
        return PaymentMethod.CREDIT_CARD;
    }

    @Override
    public ProviderResponse processPayment(Payment payment) {
        try {

            // simulate latency
            Thread.sleep(2000);

            if(payment.getPaymentMethod() != PaymentMethod.CREDIT_CARD){
                log.warn("Unsupported payment method for payment {}", payment.getPaymentId());
                return ProviderResponse.builder()
                        .success(false)
                        .status(PaymentStatus.FAILED)
                        .message("Unsupported payment method")
                        .errorCode("UNSUPPORTED_METHOD")
                        .processedAt(Instant.now())
                        .build();
            }

            if (!isHealthy()) {
                log.error("Provider is not healthy for payment {}", payment.getPaymentId());
                return ProviderResponse.builder()
                        .success(false)
                        .status(PaymentStatus.FAILED)
                        .message("Provider unavailable")
                        .errorCode("PROVIDER_DOWN")
                        .processedAt(Instant.now())
                        .build();
            }

            log.info("Payment successful: {}", payment.getPaymentId());

            return ProviderResponse.builder()
                    .success(true)
                    .externalTransactionId("stripe_" + UUID.randomUUID())
                    .status(PaymentStatus.COMPLETED)
                    .message("Payment processed successfully")
                    .processedAt(Instant.now())
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            log.error("Payment interrupted: {}", payment.getPaymentId(), e);

            return ProviderResponse.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Payment interrupted")
                    .errorCode("INTERRUPTED")
                    .processedAt(Instant.now())
                    .build();
        }
    }

    @Override
    public ProviderResponse refundPayment(Payment payment, BigDecimal amount) {
        if(!isHealthy()){
            return ProviderResponse.builder()
                    .success(false)
                    .status(PaymentStatus.FAILED)
                    .message("Payment provider is down")
                    .errorCode("PAYMENT PROVIDER NOT HEALTHY")
                    .processedAt(Instant.now())
                    .build();
        }
        return ProviderResponse.builder()
                .success(true)
                .externalTransactionId("refund_" + UUID.randomUUID())
                .status(PaymentStatus.REFUNDED)
                .message("Payment refunded successfuly")
                .processedAt(Instant.now())
                .build();
    }

    @Override
    public boolean isHealthy() {
        return true;
    }
}
