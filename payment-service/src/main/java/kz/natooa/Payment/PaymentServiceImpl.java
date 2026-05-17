package kz.natooa.Payment;

import kz.natooa.dto.*;
import kz.natooa.enums.*;
import kz.natooa.events.OrderCreatedEvent;
import kz.natooa.events.PaymentEventPublisher;
import kz.natooa.exception.PaymentAlreadyProcessedException;
import kz.natooa.payment.enums.PaymentMethod;
import kz.natooa.payment.enums.TransactionStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PaymentServiceImpl implements PaymentService{
    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final PaymentRefundRepository refundRepository;
    private final PaymentProviderFactory providerFactory;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher eventPublisher;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              PaymentTransactionRepository transactionRepository,
                              PaymentRefundRepository refundRepository,
                              PaymentProviderFactory providerFactory,
                              PaymentMapper paymentMapper,
                              PaymentEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.transactionRepository = transactionRepository;
        this.refundRepository = refundRepository;
        this.providerFactory = providerFactory;
        this.paymentMapper = paymentMapper;
        this.eventPublisher = eventPublisher;
    }


    @Retryable(
            value = {RuntimeException.class},
            exclude = {PaymentAlreadyProcessedException.class, DataIntegrityViolationException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000)
    )
    @Override
    public void processPayment(OrderCreatedEvent request) throws PaymentAlreadyProcessedException {
        validatePaymentRequest(request);

        Payment payment = createPayment(request);

        try{
            PaymentProvider provider = providerFactory.getProvider(payment.getPaymentMethod());

            ProviderResponse providerResponse = provider.processPayment(payment);

            updatePaymentStatus(payment, providerResponse);

            PaymentTransaction transaction = createTransaction(payment, providerResponse);

            eventPublisher.publishPaymentCompleted(transaction);

            paymentMapper.paymentToPaymentResponse(payment);
        }catch (RuntimeException e){
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);

            eventPublisher.publishPaymentFailed(payment);

            throw new RuntimeException("Payment processing failed", e);
        }
    }

    @Override
    public CompletableFuture<RefundResponse> processRefund(RefundRequest request) {
        return null;
    }






    private void validatePaymentRequest(OrderCreatedEvent request) throws PaymentAlreadyProcessedException {
        if(request.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID is required");
        }
        if(paymentRepository.findByOrderId(request.getOrderId()).isPresent()) {
            throw new PaymentAlreadyProcessedException("Order already processed");
        }
        if(request.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        if(request.getTotalPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
//        if(request.paymentMethod() != PaymentMethod.CREDIT_CARD) {
//            throw new IllegalArgumentException("Unsupported payment method");
//        }
    }

//    private void validateRefundRequest(RefundRequest request) {
//
//    }

    private Payment createPayment(OrderCreatedEvent request) {
        return paymentRepository.findByOrderId(request.getOrderId())
                .orElseGet(() -> {
                    var payment = Payment.builder()
                            .paymentId(UUID.randomUUID().toString())
                            .orderId(request.getOrderId())
                            .userId(request.getUserId())
                            .amount(request.getTotalPrice())
                            .currency(request.getCurrency().toString())
                            .paymentStatus(PaymentStatus.PENDING)
                            .paymentMethod(PaymentMethod.CREDIT_CARD)
                            .createdAt(Instant.now())
                            .updatedAt(Instant.now())
                            .processedAt(null)
                            .build();
                    return paymentRepository.save(payment);
                });
    }

    private void updatePaymentStatus(Payment payment, ProviderResponse response) {
        payment.setPaymentStatus(
                response.success() ? PaymentStatus.COMPLETED : PaymentStatus.FAILED
        );
        payment.setExternalTransactionId(response.externalTransactionId());
        if(!response.success()) {
            payment.setFailureReason(response.message());
        }
        payment.setUpdatedAt(Instant.now());
        payment.setProcessedAt(response.processedAt());
        paymentRepository.save(payment);
    }

    private PaymentTransaction createTransaction(Payment payment, ProviderResponse response) {
        PaymentTransaction transaction = PaymentTransaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .payment(payment)
                .type(TransactionType.CHARGE)
                .amount(payment.getAmount())
                .status(response.success() ? TransactionStatus.SUCCESS : TransactionStatus.FAILED)
                .externalTransactionId(response.externalTransactionId())
                .providerResponse(response.message())
                .createdAt(Instant.now())
                .completedAt(response.success() ? Instant.now() : null)
                .build();

        transactionRepository.save(transaction);
        return transaction;
    }
}
