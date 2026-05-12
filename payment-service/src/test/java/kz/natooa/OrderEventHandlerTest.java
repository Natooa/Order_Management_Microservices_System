package kz.natooa;

import kz.natooa.events.DeduplicationService;
import kz.natooa.events.OrderCreatedEvent;
import kz.natooa.events.OrderEventHandler;
import kz.natooa.events.PayloadToOrderCreatedEvent;
import kz.natooa.Payment.PaymentService;
import kz.natooa.exception.PaymentAlreadyProcessedException;
import kz.natooa.payment.enums.AvailableCurrency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventHandlerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PayloadToOrderCreatedEvent payloadMapper;

    @Mock
    private DeduplicationService deduplicationService; // <-- заменили processedMessageRepository

    @InjectMocks
    private OrderEventHandler orderEventHandler;

    private OrderCreatedEvent testEvent;
    private static final String TEST_PAYLOAD = "{\"orderId\":\"order-123\"}";

    @BeforeEach
    void setUp() {
        testEvent = new OrderCreatedEvent(
                "order-123",
                "user-456",
                new BigDecimal("100.00"),
                AvailableCurrency.USD
        );
    }

    @Test
    @DisplayName("Новое сообщение — вызывает processPayment")
    void handleOrderCreatedEvent_firstTime_savesAndProcessesPayment() throws PaymentAlreadyProcessedException {
        when(payloadMapper.payloadToOrderCreatedEvent(TEST_PAYLOAD)).thenReturn(testEvent);
        when(deduplicationService.tryMarkAsProcessed(testEvent.getEventId().toString())).thenReturn(true);

        orderEventHandler.handleOrderCreatedEvent(TEST_PAYLOAD);

        verify(deduplicationService).tryMarkAsProcessed(testEvent.getEventId().toString());
        verify(paymentService).processPayment(testEvent);
    }

    @Test
    @DisplayName("Дубликат — processPayment НЕ вызывается")
    void handleOrderCreatedEvent_duplicate_skipsPaymentProcessing() throws PaymentAlreadyProcessedException {
        when(payloadMapper.payloadToOrderCreatedEvent(TEST_PAYLOAD)).thenReturn(testEvent);
        when(deduplicationService.tryMarkAsProcessed(testEvent.getEventId().toString())).thenReturn(false);

        orderEventHandler.handleOrderCreatedEvent(TEST_PAYLOAD);

        verify(paymentService, never()).processPayment(any());
    }

    @Test
    @DisplayName("Дубликат — метод завершается без исключений")
    void handleOrderCreatedEvent_duplicate_doesNotThrow() {
        when(payloadMapper.payloadToOrderCreatedEvent(TEST_PAYLOAD)).thenReturn(testEvent);
        when(deduplicationService.tryMarkAsProcessed(testEvent.getEventId().toString())).thenReturn(false);

        assertDoesNotThrow(() -> orderEventHandler.handleOrderCreatedEvent(TEST_PAYLOAD));
    }

    @Test
    @DisplayName("Два уникальных события — deduplicationService вызывается дважды с разными eventId")
    void handleOrderCreatedEvent_twoUniqueEvents_callsDeduplicationForEach() throws PaymentAlreadyProcessedException {
        OrderCreatedEvent event2 = new OrderCreatedEvent(
                "order-999", "user-456", new BigDecimal("50.00"), AvailableCurrency.USD
        );
        String payload2 = "{\"orderId\":\"order-999\"}";

        when(payloadMapper.payloadToOrderCreatedEvent(TEST_PAYLOAD)).thenReturn(testEvent);
        when(payloadMapper.payloadToOrderCreatedEvent(payload2)).thenReturn(event2);
        when(deduplicationService.tryMarkAsProcessed(testEvent.getEventId().toString())).thenReturn(true);
        when(deduplicationService.tryMarkAsProcessed(event2.getEventId().toString())).thenReturn(true);

        orderEventHandler.handleOrderCreatedEvent(TEST_PAYLOAD);
        orderEventHandler.handleOrderCreatedEvent(payload2);

        verify(deduplicationService).tryMarkAsProcessed(testEvent.getEventId().toString());
        verify(deduplicationService).tryMarkAsProcessed(event2.getEventId().toString());
        verify(paymentService, times(2)).processPayment(any());
    }

    @Test
    @DisplayName("processPayment бросает PaymentAlreadyProcessedException — пробрасывается наружу")
    void handleOrderCreatedEvent_paymentAlreadyProcessed_throwsException() throws PaymentAlreadyProcessedException {
        when(payloadMapper.payloadToOrderCreatedEvent(TEST_PAYLOAD)).thenReturn(testEvent);
        when(deduplicationService.tryMarkAsProcessed(testEvent.getEventId().toString())).thenReturn(true);
        doThrow(new PaymentAlreadyProcessedException("already processed"))
                .when(paymentService).processPayment(testEvent);

        assertThrows(
                PaymentAlreadyProcessedException.class,
                () -> orderEventHandler.handleOrderCreatedEvent(TEST_PAYLOAD)
        );
    }
}