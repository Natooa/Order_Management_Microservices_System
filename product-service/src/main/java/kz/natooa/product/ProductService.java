package kz.natooa.product;


import kz.natooa.common.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    Product addProduct(Product product);
    Product updateProduct(String id, ProductDTO productToUpdate);
    void removeProduct(String id);
    Product getProductById(String id);

    PagedResponse<ProductDTO> response (List<ProductDTO> dtoList, Page<Product> page);
    Page<Product> search(String q, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    Page<Product> findByNameIgnoreCase(String name, Pageable pageable);
}
