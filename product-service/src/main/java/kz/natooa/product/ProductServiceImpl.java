package kz.natooa.product;

import kz.natooa.common.exception.ProductNotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;


public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String id, Product product) {
        return null;
    }

    @Override
    public void removeProduct(String id) {
        if(!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product with ID: " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(String id) {
        return notFound(() -> productRepository.findById(id), "Product with ID: " + id + "not found");
    }

    @Override
    public Product findByNameIgnoreCase(String name) {
        return notFound(() -> productRepository.findByNameIgnoreCase(name), "Product with name: " + name + " not found");
    }

    @Override
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }


    private Product notFound(Supplier<Optional<Product>> supplier, String message){
        return supplier.get().orElseThrow(() -> new ProductNotFoundException(message));
    }
}
