package kz.natooa.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService  productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product productToAdd) {
        return ResponseEntity.ok(productService.addProduct(productToAdd));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id,
                                                 @RequestBody Product productToUpdate) {
        var updateProduct = productService.updateProduct(id, productToUpdate);
        return ResponseEntity.ok(updateProduct);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.removeProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/findByNameIgnoreCase")
    public ResponseEntity<PagedModel<Product>> findByNameIgnoreCase(
            @RequestParam String name,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        int validatedSize = (size < 1) ? 20 : size;
        Pageable pageable = PageRequest.of(page, validatedSize);
        return ResponseEntity.ok(productService.findByNameIgnoreCase(name, pageable));
    }

    @GetMapping("/all")
    public ResponseEntity<PagedModel<Product>> findAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        int validatedSize = (size < 1) ? 20 : size;
        Pageable pageable = PageRequest.of(page, validatedSize);
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    @GetMapping("/findByPriceBetween")
    public ResponseEntity<PagedModel<Product>> findByPriceBetween(
            @RequestParam(value = "minPrice", defaultValue = "0") Double minPrice,
            @RequestParam(value = "maxPrice", defaultValue = "10000") Double maxPrice,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        int validatedSize = (size < 1) ? 20 : size;
        Pageable pageable = PageRequest.of(page, validatedSize);
        return ResponseEntity.ok(productService.findByPriceBetween(minPrice, maxPrice, pageable));
    }

    @GetMapping("/findByCategory")
    public ResponseEntity<PagedModel<Product>> findByCategory(
            @RequestParam(value = "category", defaultValue = "") String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ){
        int validatedSize = (size < 1) ? 20 : size;
        Pageable pageable = PageRequest.of(page, validatedSize);
        return ResponseEntity.ok(productService.findByCategory(category, pageable));
    }
}
