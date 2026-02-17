package kz.natooa.product;


import lombok.Data;

import java.util.Map;

@Data
public class ProductDTO {
    private String id;
    private String name;
    private Double price;
    private Double avgRating;
    private String category;
    private String description;
    private Map<String, Object> attributes;
    private String imageUrl;
}
