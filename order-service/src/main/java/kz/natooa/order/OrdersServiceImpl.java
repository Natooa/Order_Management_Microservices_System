package kz.natooa.order;

import kz.natooa.events.DomainEvent;
import kz.natooa.events.OrderCreatedEvent;
//import kz.natooa.events.OrderEventPublisher;
import kz.natooa.events.PaymentCompletedEvent;
import kz.natooa.events.PaymentFailedEvent;
import kz.natooa.grpc.InventoryGrpcClient;
import kz.natooa.grpc.PricingService;
import kz.natooa.grpc.ProductGrpcClient;
import kz.natooa.orderItems.OrderItem;
import kz.natooa.orderItems.OrderItemMapper;
import kz.natooa.outbox.OrderToOutboxMapper;
import kz.natooa.outbox.Outbox;
import kz.natooa.outbox.OutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import kz.natooa.grpc.InventoryAdapter;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
public class OrdersServiceImpl implements OrdersService{
    private final ProductGrpcClient productGrpcClient;
    private final InventoryGrpcClient inventoryGrpcClient;
    private final OrdersRepository ordersRepository;
    private final OrderItemMapper orderItemMapper;
    private final PricingService pricingService;
    private final InventoryAdapter inventoryAdapter;
//    private final OrderEventPublisher orderEventPublisher;
    private final OutboxRepository outboxRepository;
    private final OrderToOutboxMapper outboxMapper;

    public OrdersServiceImpl(OrdersRepository ordersRepository, OrderItemMapper orderItemMapper, ProductGrpcClient productGrpcClient, InventoryGrpcClient inventoryGrpcClient, PricingService pricingService, InventoryAdapter inventoryAdapter, OutboxRepository outboxRepository, OrderToOutboxMapper outboxMapper) {
        this.ordersRepository = ordersRepository;
        this.orderItemMapper = orderItemMapper;
        this.productGrpcClient = productGrpcClient;
        this.inventoryGrpcClient = inventoryGrpcClient;
        this.pricingService = pricingService;
        this.inventoryAdapter = inventoryAdapter;
//        this.orderEventPublisher = orderEventPublisher;
        this.outboxRepository = outboxRepository;
        this.outboxMapper = outboxMapper;
    }

    @Transactional
    @Override
    public OrderResponse createOrder(String userId, OrderRequestDTO requestDTO) throws ExecutionException, InterruptedException {

        if (userId == null || requestDTO.items() == null || requestDTO.items().isEmpty() || requestDTO.currency() == null) {
            throw new IllegalArgumentException("Invalid input");
        }

        List<OrderItem> items = orderItemMapper.toEntityList(requestDTO.items());

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {
            BigDecimal price = pricingService.calculatePrice(
                    item.getProductId(),
                    item.getQuantity()
            );

            item.setPriceAtPurchase(price);
            total = total.add(price);
        }

        Orders order = Orders.builder()
                .userId(userId)
                .status(Status.CREATED)
                .totalPrice(total)
                .orderItems(items)
                .currency(requestDTO.currency())
                .build();

        items.forEach(i -> i.setOrder(order));

        Orders saved = ordersRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
          order.getId().toString(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getCurrency()
        );

        Outbox outbox = outboxMapper.map(event);
        outboxRepository.save(outbox);

        inventoryAdapter.reserve(saved);

//        orderEventPublisher.publishOrderCreated(order);

        log.info("Order ID: {}", order.getId());

        log.info("Order status: {}", order.getStatus());

        return OrderMapper.toResponse(saved);
    }

//    @Override
//    public OrderResponse createOrder(String userId, List<OrderItemRequestDTO> orderItems) {
//        if(userId == null || orderItems == null || orderItems.isEmpty()){
//            throw new IllegalArgumentException("User ID, order items, and order items must not be null");
//        }
//        List<OrderItem> createOrderItems = new ArrayList<>();
//        BigDecimal totalPrice = BigDecimal.ZERO;
//
//        for(OrderItemRequestDTO dto : orderItems){
//            ProductResponse product = productGrpcClient.getProduct(dto.productId);
//
//            BigDecimal price = BigDecimal.valueOf(product.getPrice().getUnits())
//                    .add(BigDecimal.valueOf(product.getPrice().getNanos(), 9));
//
//            OrderItem item = OrderItem.builder()
//                    .productId(dto.productId)
//                    .quantity(dto.quantity)
//                    .priceAtPurchase(price)
//                    .build();
//
//            createOrderItems.add(item);
//            totalPrice = totalPrice.add(price.multiply(BigDecimal.valueOf(dto.quantity)));
//        }
//
//        Orders newOrder = Orders.builder()
//                .userId(userId)
//                .status(Status.CREATED)
//                .totalPrice(totalPrice)
//                .orderItems(createOrderItems)
//                .build();
//
//        for (OrderItem item : createOrderItems) {
//            item.setOrder(newOrder);
//        }
//
//        Orders savedOrder = ordersRepository.save(newOrder);
//
//        for(OrderItem item : savedOrder.getOrderItems()){
//            inventoryGrpcClient.reserveInventory(item.getProductId(), item.getQuantity(), savedOrder.getId().toString());
//        }
//
//        return savedOrder;
//    }

    @Override
    public void cancelOrder(PaymentFailedEvent event) {

        Orders order = validateOrder(event.getOrderId());

        order.setStatus(Status.CANCELED);
        ordersRepository.save(order);

        try {
            inventoryAdapter.cancelReservation(event.getOrderId());
        } catch (Exception e) {
            // лог + компенсация
            throw new RuntimeException("Failed to cancel inventory reservation", e);
        }
    }

    @Override
    public void confirmOrder(PaymentCompletedEvent event) {

        Orders order = validateOrder(event.getOrderId());

        Status previousStatus = order.getStatus();

        order.setStatus(Status.COMPLETED);
        ordersRepository.save(order);

        try {
            inventoryAdapter.confirmReservation(event.getOrderId());
        } catch (Exception e) {
            // rollback status (compensation)
            order.setStatus(previousStatus);
            ordersRepository.save(order);

            throw new RuntimeException("Failed to confirm inventory reservation", e);
        }
    }

    @Override
    public Orders updateOrderStatus(String orderId, Status status) {
        return null;
    }


    private Orders validateOrder(String orderId){
        if (orderId == null) {
            throw new IllegalArgumentException("Order ID must not be null");
        }

        Orders order = ordersRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus().equals(Status.COMPLETED)) {
            throw new IllegalStateException("Order already confirmed");
        }
        if (order.getStatus().equals(Status.CANCELED)) {
            throw new IllegalStateException("Order already canceled");
        }
        return order;
    }
}
