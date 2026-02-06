package kz.natooa.review;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "reviews")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Review {
    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String productId;
    private String userId;
    private double rating;
    private String comment;

    private Instant createdAt;
}
