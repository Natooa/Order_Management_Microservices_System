package kz.natooa.product;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "products")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Indexed
    private String name;

    private double price;
    private double avgRating;
    private String category;
    private String description;

    private Map<String, Object> attributes;

    private String imageUrl;

    public Product(String name, double price, double avgRating, String category, String description, Map<String, Object> attributes, String imageUrl) {
        this.name = name;
        this.price = price;
        this.avgRating = avgRating;
        this.category = category;
        this.description = description;
        this.attributes = attributes;
        this.imageUrl = imageUrl;
    }
}
