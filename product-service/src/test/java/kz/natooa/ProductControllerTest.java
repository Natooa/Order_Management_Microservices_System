package kz.natooa;

import kz.natooa.product.ProductController;
import kz.natooa.product.ProductMapper;
import kz.natooa.product.ProductMapperImpl;
import kz.natooa.product.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    @Mock
    ProductService productService;

    @Spy
    ProductMapper productMapper = new ProductMapperImpl();

    @InjectMocks
    ProductController productController;

    @Test
    void shouldReturnProducts(){

    }
}
