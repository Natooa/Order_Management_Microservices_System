package kz.natooa.orderItems;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import kz.natooa.order.Orders;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderItem {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    @Column(name = "product_id", nullable = false)
    private  String productId;

    @Column(name = "quantity", nullable = false)
    @Min(1)
    private  Integer quantity;

    @Column(name = "price_at_purchase", nullable = false, updatable = false)
    @Min(1)
    private BigDecimal priceAtPurchase;
}
