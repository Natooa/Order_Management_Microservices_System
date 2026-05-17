package kz.natooa.entity;

import jakarta.persistence.*;
import kz.natooa.dto.ShipmentStatus;
import lombok.*;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shipments {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(nullable = false, unique = true)
    private String orderId;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

    @Column(nullable = false, unique = true)
    private String trackingNumber;

    @Column(nullable = false)
    private String carrier;

    @Column(nullable = false)
    private String estimatedDeliveryDate;

    @Column(nullable = false)
    private String estimatedDeliveryTime;

}
