package kz.natooa.Payment;

import kz.natooa.dto.PaymentRequest;
import kz.natooa.dto.PaymentResponse;
import kz.natooa.dto.RefundRequest;
import kz.natooa.dto.RefundResponse;
import kz.natooa.events.OrderCreatedEvent;
import kz.natooa.exception.PaymentAlreadyProcessedException;

import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    PaymentResponse processPayment(OrderCreatedEvent request) throws PaymentAlreadyProcessedException;
    PaymentResponse getPaymentStatus(String paymentId);
    CompletableFuture<RefundResponse> processRefund(RefundRequest request);
}
