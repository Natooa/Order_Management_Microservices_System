package kz.natooa.product;

import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.google.type.Decimal;
import com.google.type.Money;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.Map;

@GrpcService
public class ProductServiceGrpcImpl extends ProductServiceGrpc.ProductServiceImplBase{
    private final ProductService productService;

    public ProductServiceGrpcImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void getProduct(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        try{
            var product = productService.getProductById(request.getId());

            BigDecimal price = product.getPrice();
            Money money = Money.newBuilder()
                    .setCurrencyCode("KZT")
                    .setUnits(price.longValue())
                    .setNanos(price.remainder(BigDecimal.ONE)
                            .movePointRight(9)
                            .intValue())
                    .build();

            Map<String, Value> attributes = convertAttribute(product);

            var productResponse = ProductResponse.newBuilder()
                    .setProductId(product.getId())
                    .setName(product.getName())
                    .setPrice(money)
                    .setCategory(product.getCategory())
                    .setDescription(product.getDescription())
                    .putAllAttributes(attributes)
                    .setImageUrl(product.getImageUrl())
                    .build();

            responseObserver.onNext(productResponse);
            responseObserver.onCompleted();
        } catch (Exception e){
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    private Map<java.lang.String, com.google.protobuf.Value> convertAttribute(Product product) {
        com.google.protobuf.Struct.Builder attributesBuilder = com.google.protobuf.Struct.newBuilder();
        if (product.getAttributes() != null) {
            product.getAttributes().forEach((key, value) -> {
                com.google.protobuf.Value protoValue = convertToProtoValue(value);
                attributesBuilder.putFields(key, protoValue);
            });
        }
        return attributesBuilder.getFieldsMap();
    }

    private com.google.protobuf.Value convertToProtoValue(Object value) {
        if (value == null) {
            return com.google.protobuf.Value.newBuilder()
                    .setNullValue(com.google.protobuf.NullValue.NULL_VALUE)
                    .build();
        } else if (value instanceof String s) {
            return com.google.protobuf.Value.newBuilder()
                    .setStringValue(s)
                    .build();
        } else if (value instanceof Number n) {
            return com.google.protobuf.Value.newBuilder()
                    .setNumberValue(n.doubleValue())
                    .build();
        } else if (value instanceof Boolean b) {
            return com.google.protobuf.Value.newBuilder()
                    .setBoolValue(b)
                    .build();
        } else {
            // для остальных типов — toString
            return com.google.protobuf.Value.newBuilder()
                    .setStringValue(value.toString())
                    .build();
        }
    }

}
