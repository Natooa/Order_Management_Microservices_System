package kz.natooa.service;

import kz.natooa.dto.ShipmentStatus;
import kz.natooa.entity.Shipments;
import kz.natooa.events.PaymentCompletedEvent;
import kz.natooa.payment.enums.TransactionStatus;
import kz.natooa.repository.ShipmentsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Slf4j
public class ShipmentsServiceImpl implements ShipmentsService {

    private final ShipmentsRepository shipmentsRepository;

    public ShipmentsServiceImpl(ShipmentsRepository shipmentsRepository) {
        this.shipmentsRepository = shipmentsRepository;
    }

    @Override
    public void processShipments(PaymentCompletedEvent request) {
        validateShipmentRequest(request);

        log.info("Creating shipment for order: {}", request.getOrderId());

        Shipments shipment = buildShipment(request);

        
    }

    @Override
    public void updateShipmentStatus(String orderId) {

    }

    @Override
    public void cancelShipment(String orderId) {

    }

    @Override
    public void confirmShipment(String orderId) {

    }

    @Override
    public void assignDelivery(String orderId) {

    }

    private Shipments buildShipment(PaymentCompletedEvent event){
        return shipmentsRepository.findByOrderId(event.getOrderId())
                .orElseGet(() -> {
                    var shipment = Shipments.builder()
                            .orderId(event.getOrderId())
                            .status(ShipmentStatus.PENDING)
                            .trackingNumber(UUID.randomUUID().toString())
                            .carrier(shipmentCarrier())
                            .estimatedDeliveryDate(
                                    Instant.now().plus(5, ChronoUnit.DAYS).toString()
                            )
                            .estimatedDeliveryTime(Instant.now().toString())
                            .build();

                    return shipmentsRepository.save(shipment);
                });
    }

    private void validateShipmentRequest(PaymentCompletedEvent event) {
        if(event.getOrderId() == null){
            throw new IllegalArgumentException("Order ID is required");
        }
        if(shipmentsRepository.findByOrderId(event.getOrderId()).isPresent()){
            throw new IllegalArgumentException("Shipment already exists for this order");
        }
        if(event.getStatus().equals(TransactionStatus.FAILED) || event.getStatus().equals(TransactionStatus.CANCELLED)){
            throw new IllegalArgumentException("Cannot create shipment for cancelled or failed transactions");
        }
    }

    private String shipmentCarrier(){
        return "CARRIER_" + UUID.randomUUID().toString().substring(0, 4);
    }
}
