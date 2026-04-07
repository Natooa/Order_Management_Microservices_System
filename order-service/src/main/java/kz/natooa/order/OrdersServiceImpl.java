package kz.natooa.order;

import kz.natooa.orderItems.OrderItem;
import kz.natooa.orderItems.OrderItemMapper;
import kz.natooa.orderItems.OrderItemRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersServiceImpl implements OrdersService{
    private final OrdersRepository ordersRepository;
    private final OrderItemMapper orderItemMapper;

    public OrdersServiceImpl(OrdersRepository ordersRepository, OrderItemMapper orderItemMapper) {
        this.ordersRepository = ordersRepository;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public Orders createOrder(String userId, List<OrderItemRequestDTO> orderItems) {
        if(userId == null || orderItems == null || orderItems.isEmpty()){
            throw new IllegalArgumentException("User ID, order items, and order items must not be null");
        }
        List<OrderItem> createOrderItems = orderItemMapper.orderDTOListToOrderItems(orderItems);
        Orders newOrder = Orders.builder()
                .userId(userId)
                .status(Status.CREATED)
                .build();

        for(OrderItem orderItem : createOrderItems){
            orderItem.setOrder(newOrder);
        }
        return ordersRepository.save(newOrder);
    }

    @Override
    public Orders cancelOrder(String orderId) {
        return null;
    }

    @Override
    public Orders updateOrderStatus(String orderId, Status status) {
        return null;
    }
}
