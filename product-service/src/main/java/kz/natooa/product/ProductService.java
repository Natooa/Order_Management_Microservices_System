package kz.natooa.product;


import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;

public interface ProductService {
    Product addProduct(Product product);
    Product updateProduct(String id, Product productToUpdate);
    void removeProduct(String id);
    Product getProductById(String id);


    PagedModel<Product> findAll(Pageable pageable);
    PagedModel<Product> findByNameIgnoreCase(String name, Pageable pageable);
    PagedModel<Product> findByCategory(String category, Pageable pageable);
    PagedModel<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);
}
