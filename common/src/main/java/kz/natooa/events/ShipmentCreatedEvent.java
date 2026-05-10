package kz.natooa.events;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ShipmentCreatedEvent extends DomainEvent{
    String orderId;
    String shipmentId;
    String trackingNumber;

    public ShipmentCreatedEvent(String orderId, String shipmentId, String trackingNumber) {
        this.orderId = orderId;
        this.shipmentId = shipmentId;
        this.trackingNumber = trackingNumber;
    }
}
