package kz.natooa.product;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;


import java.util.Map;

@Document(collection = "products")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Indexed
    @NotBlank(message = "Product name cannot be blank")
    @Size(max=255, message = "Product name cannot be longer than 255 characters")
    private String name;

    @DecimalMin(value = "0.0", inclusive = false ,message = "Price cannot be less than 0.0")
    private Double price;

    @DecimalMin(value = "0.0", message = "Rating cannot be less than 0.0")
    @DecimalMax(value = "5.0", message = "Rating cannot be more than 5.0")
    private Double avgRating;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    private String description;
    private Map<String, Object> attributes;
    private String imageUrl;

}
