package kz.natooa.dto;

import kz.natooa.enums.PaymentMethod;
import kz.natooa.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentResponse(
        String paymentId,
        String orderId,
        String userId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        PaymentMethod paymentMethod,
        String externalTransactionId,
        String failureReason,
        Instant createdAt,
        Instant processedAt
) {
}
