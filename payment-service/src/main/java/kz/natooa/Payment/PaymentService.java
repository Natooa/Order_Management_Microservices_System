package kz.natooa.Payment;

import kz.natooa.dto.RefundRequest;
import kz.natooa.dto.RefundResponse;
import kz.natooa.events.OrderCreatedEvent;
import kz.natooa.exception.PaymentAlreadyProcessedException;

import java.util.concurrent.CompletableFuture;

public interface PaymentService {
    void processPayment(OrderCreatedEvent request) throws PaymentAlreadyProcessedException;

    CompletableFuture<RefundResponse> processRefund(RefundRequest request);
}
