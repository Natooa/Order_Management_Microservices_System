package kz.natooa.grpc;

import kz.natooa.product.ProductResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PricingService {

    private final ProductGrpcClient productGrpcClient;

    public PricingService(ProductGrpcClient productGrpcClient) {
        this.productGrpcClient = productGrpcClient;
    }

    public BigDecimal calculatePrice(String productId, Integer quantity) {

        ProductResponse product = productGrpcClient.getProduct(productId);

        BigDecimal price = BigDecimal.valueOf(product.getPrice().getUnits())
                .add(BigDecimal.valueOf(product.getPrice().getNanos(), 9));

        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
