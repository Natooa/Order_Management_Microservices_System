package kz.natooa.dto;

import kz.natooa.payment.enums.PaymentMethod;
import kz.natooa.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record RefundResponse(
        String paymentId,
        String orderId,
        String userId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        PaymentMethod paymentMethod,
        String externalTransactionId,
        String failureReason,
        Instant processedAt) {
}
