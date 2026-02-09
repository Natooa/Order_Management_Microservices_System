package kz.natooa.product;


import java.util.List;

public interface ProductService {
    Product addProduct(Product product);
    Product updateProduct(String id, Product productToUpdate);
    void removeProduct(String id);
    Product getProductById(String id);
    List<Product> getAllProducts();
    Product findByNameIgnoreCase(String name);
}
