package kz.natooa.dto;

import kz.natooa.enums.PaymentStatus;
import lombok.Builder;

import java.util.Map;

@Builder
public record ProviderResponse(
        boolean success,
        String externalTransactionId,
        PaymentStatus status,
        String message,
        String errorCode,
        Map<String, Object> additionalData
) {
}
