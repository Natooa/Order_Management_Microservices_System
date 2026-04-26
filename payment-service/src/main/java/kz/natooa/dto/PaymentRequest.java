package kz.natooa.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import kz.natooa.enums.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequest (
        @NotNull
        String orderId,

        @NotNull
        String userId,

        @NotNull
        @DecimalMin(value = "0.01", message = "Amount must be at least 0")
        BigDecimal amount,

        @NotNull
        @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters long")
        String currency,

        @NotNull
        PaymentMethod paymentMethod,

        PaymentMethodDetails paymentMethodDetails
){
}
