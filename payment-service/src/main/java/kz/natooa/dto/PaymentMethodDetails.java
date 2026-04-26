package kz.natooa.dto;

import lombok.Builder;

@Builder
public record PaymentMethodDetails(
        String cardNumber,
        String cardHolderName,
        String expirationDate,
        String paypalEmail,
        String bankAccountNumber
) {
}
