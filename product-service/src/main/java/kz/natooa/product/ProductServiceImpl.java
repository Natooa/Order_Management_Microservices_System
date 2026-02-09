package kz.natooa.product;

import kz.natooa.common.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addProduct(Product product) {
        if(product == null) {
            throw new IllegalArgumentException("Product can't be null");
        }
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String id, Product productToUpdate) {
        if(productToUpdate == null) {
            throw new IllegalArgumentException("Product can't be null");
        }

        Product oldProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID: " + id + " not found"));

        if(productToUpdate.getName() != null){
            oldProduct.setName(productToUpdate.getName());
        }
        if(productToUpdate.getPrice() != null) {
            oldProduct.setPrice(productToUpdate.getPrice());
        }
        if(productToUpdate.getAvgRating() != null) {
            oldProduct.setAvgRating(productToUpdate.getAvgRating());
        }
        if(productToUpdate.getCategory() != null) {
            oldProduct.setCategory(productToUpdate.getCategory());
        }
        if(productToUpdate.getDescription() != null) {
            oldProduct.setDescription(productToUpdate.getDescription());
        }
        if(productToUpdate.getAttributes() != null) {
            oldProduct.setAttributes(productToUpdate.getAttributes());
        }
        if(productToUpdate.getImageUrl() != null) {
            oldProduct.setImageUrl(productToUpdate.getImageUrl());
        }

        return productRepository.save(oldProduct);
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
        return notFound(() -> productRepository.findById(id), "Product with ID: " + id + " not found");
    }

    @Override
    public Product findByNameIgnoreCase(String name) {
        return notFound(() -> productRepository.findByNameIgnoreCase(name), "Product with name: " + name + " not found");
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    private Product notFound(Supplier<Optional<Product>> supplier, String message){
        return supplier.get().orElseThrow(() -> new ProductNotFoundException(message));
    }
}