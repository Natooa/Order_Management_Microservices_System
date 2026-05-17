package kz.natooa.service;

import kz.natooa.events.PaymentCompletedEvent;

public interface ShipmentsService {
    void processShipments(PaymentCompletedEvent event);

    void updateShipmentStatus(String orderId);

    void cancelShipment(String orderId);

    void confirmShipment(String orderId);

    void assignDelivery(String orderId);
}
