package kz.natooa;

import kz.natooa.product.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    MongoTemplate mongoTemplate;

    @Spy
    ProductMapper productMapper = new ProductMapperImpl();

    @InjectMocks
    ProductServiceImpl productServiceImpl;

    @Test
    void shouldCreateProduct(){
        Product product = createFakeProduct();

        when(productRepository.save(any(Product.class)))
                .thenReturn(product);

        Product savedProduct = productServiceImpl.addProduct(product);

        assertNotNull(savedProduct);
        assertEquals("p1001", savedProduct.getId());
        assertEquals("Gaming Keyboard", savedProduct.getName());

        verify(productRepository).save(product);
    }

    @Test
    void shouldGetProductById_whenProductExists() {
        Product product = createFakeProduct();

        when(productRepository.findById("p1001"))
                .thenReturn(Optional.of(product));

        Product foundProduct = productServiceImpl.getProductById("p1001");

        assertNotNull(foundProduct);
        assertEquals("p1001", foundProduct.getId());
        assertEquals("Gaming Keyboard", foundProduct.getName());

        verify(productRepository).findById("p1001");
    }

    @Test
    void shouldUpdateProduct(){
        Product productToUpdate = createFakeProduct();

        Product newProductInfo = new Product(
                "p1001",
                "Keyboard with USB cable",
                new BigDecimal("89.99"),
                4.9,
                "electronics",
                "Mechanical RGB gaming keyboard with blue switches",
                new HashMap<>(Map.of(
                        "color", "black",
                        "switchType", "blue",
                        "connection", "USB",
                        "type", "mechanical"
                )),
                "https://example.com/images/keyboard.jpg"
        );
        ProductDTO productDTO = productMapper.productToProductDTO(newProductInfo);

        when(productRepository.findById("p1001"))
                .thenReturn(Optional.of(productToUpdate));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product updatedProduct = productServiceImpl.updateProduct("p1001", productDTO);

        assertEquals("p1001", updatedProduct.getId());
        assertEquals("Keyboard with USB cable", updatedProduct.getName());
        assertEquals(4.9, updatedProduct.getAvgRating());
        assertEquals("mechanical", updatedProduct.getAttributes().get("type"));

        verify(productRepository).save(updatedProduct);
        verify(productRepository).findById("p1001");
        verify(productMapper).updateEntityFromDto(productDTO, productToUpdate);
    }

    @Test
    void shouldReturnProducts_whenProductSearch(){
        Product product = createFakeProduct();

        when(mongoTemplate.find(any(Query.class), eq(Product.class)))
                .thenReturn(List.of(product));

        when(mongoTemplate.count(any(Query.class), eq(Product.class)))
                .thenReturn(1L);

        Page<Product> result = productServiceImpl.search("keyboard",
                "electronics",
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(100),
                Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        assertEquals("p1001", result.getContent().get(0).getId());
    }

    private Product createFakeProduct(){
        return new Product(
                "p1001",
                "Gaming Keyboard",
                new BigDecimal("89.99"),
                4.7,
                "electronics",
                "Mechanical RGB gaming keyboard with blue switches",
                new HashMap<>(Map.of(
                        "color", "black",
                        "switchType", "blue",
                        "connection", "USB"
                )),
                "https://example.com/images/keyboard.jpg"
        );
    }
}
