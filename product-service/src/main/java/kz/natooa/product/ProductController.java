package kz.natooa.product;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import kz.natooa.common.dto.PagedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService  productService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService,
                             ProductMapper productMapper) {
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostMapping("/add")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productToAdd) {
        Product product = productMapper.productDTOToProduct(productToAdd);
        Product savedProduct = productService.addProduct(product);
        return ResponseEntity.ok(productMapper.productToProductDTO(savedProduct));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") String id,
                                                 @Valid @RequestBody ProductDTO productToUpdate) {
        Product updatedProduct = productService.updateProduct(id, productToUpdate);
        ProductDTO dto = productMapper.productToProductDTO(updatedProduct);

        return ResponseEntity.ok(dto);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") String id) {
        productService.removeProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable("id") String id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(productMapper.productToProductDTO(product));
    }

    @GetMapping("/findByNameIgnoreCase")
    public ResponseEntity<PagedResponse<ProductDTO>> findByNameIgnoreCase(
            @RequestParam(name = "name") String name,
            @PageableDefault(size = 20, page = 0) Pageable pageable
    ) {
        Page<Product> page = productService.findByNameIgnoreCase(name, pageable);
        List<ProductDTO> dtoList = page.map(productMapper::productToProductDTO).getContent();

        PagedResponse<ProductDTO> response = new PagedResponse<>(
                dtoList,
                new PagedResponse.PageInfo(
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<ProductDTO>> search(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            Pageable pageable
) {
    Page<Product> page = productService.search(q, category, minPrice, maxPrice, pageable);
    List<ProductDTO> dtoList = page.map(productMapper::productToProductDTO).getContent();

        PagedResponse<ProductDTO> response = new PagedResponse<>(
                dtoList,
                new PagedResponse.PageInfo(
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );

    return ResponseEntity.ok(response);
}

}