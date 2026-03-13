package kz.natooa.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Setter
@Builder
@Getter
@Entity
@Table(name = "inventory")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inventory {
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "total_quantity", nullable = false)
    @Min(0)
    private Integer totalQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    @Min(0)
    private Integer reservedQuantity;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
