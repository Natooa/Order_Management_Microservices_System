package kz.natooa.order;

import kz.natooa.grpc.InventoryGrpcClient;
import kz.natooa.grpc.PricingService;
import kz.natooa.grpc.ProductGrpcClient;
import kz.natooa.orderItems.OrderItem;
import kz.natooa.orderItems.OrderItemMapper;
import kz.natooa.orderItems.OrderItemRequestDTO;
import org.springframework.stereotype.Service;
import kz.natooa.grpc.InventoryAdapter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class OrdersServiceImpl implements OrdersService{
    private final ProductGrpcClient productGrpcClient;
    private final InventoryGrpcClient inventoryGrpcClient;
    private final OrdersRepository ordersRepository;
    private final OrderItemMapper orderItemMapper;
    private final PricingService pricingService;
    private final InventoryAdapter inventoryAdapter;

    public OrdersServiceImpl(OrdersRepository ordersRepository, OrderItemMapper orderItemMapper, ProductGrpcClient productGrpcClient, InventoryGrpcClient inventoryGrpcClient, PricingService pricingService, InventoryAdapter inventoryAdapter) {
        this.ordersRepository = ordersRepository;
        this.orderItemMapper = orderItemMapper;
        this.productGrpcClient = productGrpcClient;
        this.inventoryGrpcClient = inventoryGrpcClient;
        this.pricingService = pricingService;
        this.inventoryAdapter = inventoryAdapter;
    }

    @Override
    public OrderResponse createOrder(String userId, List<OrderItemRequestDTO> dtos) {

        if (userId == null || dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Invalid input");
        }

        List<OrderItem> items = orderItemMapper.toEntityList(dtos);

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < items.size(); i++) {

            OrderItem item = items.get(i);
            OrderItemRequestDTO dto = dtos.get(i);

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
                .build();

        items.forEach(i -> i.setOrder(order));

        Orders saved = ordersRepository.save(order);

        inventoryAdapter.reserve(saved);

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
    public void cancelOrder(String orderId) {

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID must not be null");
        }

        Orders order = ordersRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == Status.CANCELED) {
            return; // idempotent
        }

        if (order.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("Cannot cancel confirmed order");
        }

        order.setStatus(Status.CANCELED);
        ordersRepository.save(order);

        try {
            inventoryAdapter.cancelReservation(orderId);
        } catch (Exception e) {
            // лог + компенсация
            throw new RuntimeException("Failed to cancel inventory reservation", e);
        }
    }

    @Override
    public void confirmOrder(String orderId) {

        if (orderId == null) {
            throw new IllegalArgumentException("Order ID must not be null");
        }

        Orders order = ordersRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getStatus() == Status.COMPLETED) {
            return; // idempotent
        }

        if (order.getStatus() == Status.CANCELED) {
            throw new IllegalStateException("Cannot confirm cancelled order");
        }

        order.setStatus(Status.COMPLETED);
        ordersRepository.save(order);

        try {
            inventoryAdapter.confirmReservation(orderId);
        } catch (Exception e) {
            // rollback status (compensation)
            order.setStatus(Status.CREATED);
            ordersRepository.save(order);

            throw new RuntimeException("Failed to confirm inventory reservation", e);
        }
    }

    @Override
    public Orders updateOrderStatus(String orderId, Status status) {
        return null;
    }

}
