package kz.natooa.Payment;

import kz.natooa.dto.PaymentRequest;
import kz.natooa.dto.PaymentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring"
)
public interface PaymentMapper{
//    @Mapping(source = "paymentMethodDetails", target = "paymentMethodDetails")
    PaymentRequest paymentToPaymentRequest(Payment payment);

    PaymentResponse paymentToPaymentResponse(Payment payment);
}
