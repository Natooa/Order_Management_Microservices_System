package kz.natooa.product;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;

public interface ProductService {
    Product addProduct(Product product);
    Product updateProduct(String id, ProductDTO productToUpdate);
    void removeProduct(String id);
    Product getProductById(String id);

    Page<Product> search(String q, String category, Double minPrice, Double maxPrice, Pageable pageable);
    Page<Product> findByNameIgnoreCase(String name, Pageable pageable);
}
