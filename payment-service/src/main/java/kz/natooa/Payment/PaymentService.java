package kz.natooa.Payment;

import kz.natooa.dto.PaymentRequest;
import kz.natooa.dto.PaymentResponse;

import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest request);
    PaymentResponse getPaymentStatus(String paymentId);
    CompletableFuture<RefundResponse> processRefund(RefundRequest request);
}
