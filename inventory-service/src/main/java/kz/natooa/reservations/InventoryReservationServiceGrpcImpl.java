package kz.natooa.reservations;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import kz.natooa.inventory.*;
import org.springframework.grpc.server.service.GrpcService;
import java.util.UUID;

@GrpcService
public class InventoryReservationServiceGrpcImpl extends InventoryServiceGrpc.InventoryServiceImplBase {
    private final InventoryReservationService inventoryService;

    public InventoryReservationServiceGrpcImpl(InventoryReservationService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void reserveInventory(ReserveInventoryRequest request, StreamObserver<ReserveInventoryResponse> responseObserver) {
        try {
            var response = inventoryService.reserveInventory(request.getProductId(), request.getQuantity(), UUID.fromString(request.getOrderId()));
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e){
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void cancelReservation(ReservationIdRequest request, StreamObserver<EmptyResponse> responseObserver) {
        try {
            inventoryService.cancelReservation(UUID.fromString(request.getOrderId()));
            responseObserver.onNext(EmptyResponse.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e){
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void confirmReservation(ReservationIdRequest request, StreamObserver<EmptyResponse> responseObserver) {
        try{
            inventoryService.confirmReservation(UUID.fromString(request.getOrderId()));
            responseObserver.onNext(EmptyResponse.newBuilder().build());
            responseObserver.onCompleted();
        }catch (Exception e){
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}
