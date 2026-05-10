package kz.natooa.grpc;

import io.grpc.StatusRuntimeException;
import kz.natooa.product.ProductRequest;
import kz.natooa.product.ProductResponse;
import kz.natooa.product.ProductServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProductGrpcClient {
    private final ProductServiceGrpc.ProductServiceBlockingStub stub;

    public ProductGrpcClient(GrpcChannelFactory channels) {
        this.stub = ProductServiceGrpc.newBlockingStub(
                channels.createChannel("product-service")
        );
    }

    public ProductResponse getProduct(String productId){
        try {

            log.info("Getting product: {}", productId);
            return stub
                    .withDeadlineAfter(5000, java.util.concurrent.TimeUnit.MILLISECONDS)
                    .getProduct(ProductRequest.newBuilder().setId(productId).build());
        }catch (StatusRuntimeException e){
            log.error("Error getting product: {}", productId, e);
            throw e;
        }
    }
}
