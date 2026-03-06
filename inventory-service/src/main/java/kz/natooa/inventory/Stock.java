package kz.natooa.inventory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigInteger;

@Table(name = "stock")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Stock {
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "product_id")
    private String productId;

    @Column(name = "total_quantity")
    private Integer total_quantity;

    @Column(name = "reserved_quantity")
    private Integer reserved_quantity;
}
