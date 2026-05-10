package kz.natooa.order;

import kz.natooa.orderItems.OrderItemRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrdersService ordersService;

    public OrderController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody OrderRequestDTO requestDTO
    ) throws ExecutionException, InterruptedException {
        return ResponseEntity.ok(
                ordersService.createOrder(userId, requestDTO)
        );
    }

//    @PutMapping("/cancel")
//    public ResponseEntity<String> cancelOrder(@RequestBody OrderIdRequest request) {
//        ordersService.cancelOrder(request.orderId());
//        return ResponseEntity.ok("Order cancelled");
//    }
//
//    @PutMapping("/confirm")
//    public ResponseEntity<String> confirmOrder(@RequestBody OrderIdRequest request) {
//        ordersService.confirmOrder(request.orderId());
//        return ResponseEntity.ok("Order confirmed");
//    }
}