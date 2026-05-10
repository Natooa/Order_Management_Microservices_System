package kz.natooa.grpc;

import io.grpc.StatusRuntimeException;
import kz.natooa.inventory.InventoryServiceGrpc;
import kz.natooa.inventory.ReservationIdRequest;
import kz.natooa.inventory.ReserveInventoryRequest;
import kz.natooa.inventory.ReserveInventoryResponse;
import kz.natooa.product.ProductServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class InventoryGrpcClient {
    private final InventoryServiceGrpc.InventoryServiceBlockingStub stub;

    public InventoryGrpcClient(GrpcChannelFactory channels) {
        this.stub = InventoryServiceGrpc.newBlockingStub(
                channels.createChannel("inventory-service")
        );
    }

    public ReserveInventoryResponse reserveInventory(String productId, Integer quantity, String orderId){
        try {
            log.info("Reserving inventory for product: {}, quantity: {}, order: {}", productId, quantity, orderId);
            return stub
                    .withDeadlineAfter(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
                    .reserveInventory(ReserveInventoryRequest.newBuilder().setProductId(productId).setQuantity(quantity).setOrderId(orderId).build());
        } catch (StatusRuntimeException e){
            log.error("Error reserving inventory for product: {}, quantity: {}, order: {}", productId, quantity, orderId, e);
            throw e;
        }
    }

    public void cancelReservation(String orderId){
        try{
            log.info("Canceling reservation for order: {}", orderId);
            stub
                    .withDeadlineAfter(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
                    .cancelReservation(ReservationIdRequest.newBuilder().setOrderId(orderId).build());
        } catch (StatusRuntimeException e){
            log.error("Error canceling reservation for order: {}", orderId, e);
            throw e;
        }
        }

    public void confirmReservation(String orderId){
        try{
            log.info("Confirming reservation for order: {}", orderId);
            stub
                    .withDeadlineAfter(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
                    .confirmReservation(ReservationIdRequest.newBuilder().setOrderId(orderId).build());
        }catch (StatusRuntimeException e){
            log.error("Error confirming reservation for order: {}", orderId, e);
            throw e;
        }
    }
}
