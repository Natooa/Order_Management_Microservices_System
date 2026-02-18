package kz.natooa.product;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;


import java.math.BigDecimal;
import java.util.Map;

@Document(collection = "products")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes(
        @CompoundIndex(def = "{'category': 1, 'price': 1}")
)
public class Product {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    @NotBlank(message = "Product name cannot be blank")
    @Size(max=255, message = "Product name cannot be longer than 255 characters")
    /*Created Text Index for name in MongoDB shell*/
    private String name;

    @DecimalMin(value = "0.0", inclusive = false ,message = "Price cannot be less than 0.0")
    @Indexed
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Rating cannot be less than 0.0")
    @DecimalMax(value = "5.0", message = "Rating cannot be more than 5.0")
    @Indexed
    private Double avgRating;

    @NotBlank(message = "Category cannot be blank")
    @Indexed
    private String category;

    /*Created Text Index for description in MongoDB shell*/
    private String description;
    private Map<String, Object> attributes;
    private String imageUrl;

}
