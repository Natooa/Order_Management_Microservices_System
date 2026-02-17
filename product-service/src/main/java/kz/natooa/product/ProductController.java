package kz.natooa.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService  productService;
    private final ProductMapper productMapper;
    private final ProductAssembler productAssembler;

    public ProductController(ProductService productService,
                             ProductMapper productMapper,
                             ProductAssembler productAssembler) {
        this.productService = productService;
        this.productMapper = productMapper;
        this.productAssembler = productAssembler;
    }

    @PostMapping("/add")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productToAdd) {
        Product product = productMapper.productDTOToProduct(productToAdd);
        Product savedProduct = productService.addProduct(product);
        ProductDTO responseDTO = productMapper.productToProductDTO(savedProduct);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("id") String id,
                                                 @RequestBody ProductDTO productToUpdate) {
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
    public ResponseEntity<EntityModel<ProductDTO>> getProductById(@PathVariable("id") String id) {
        Product product = productService.getProductById(id);
        ProductDTO responseDTO = productMapper.productToProductDTO(product);
        return ResponseEntity.ok(productAssembler.toModel(responseDTO));
    }

    @GetMapping("/findByNameIgnoreCase")
    public ResponseEntity<PagedModel<EntityModel<ProductDTO>>> findByNameIgnoreCase(
            @RequestParam(name = "name") String name,
            @PageableDefault(size = 20, page = 0) Pageable pageable,
            PagedResourcesAssembler<ProductDTO> assembler
    ) {
        Page<Product> page = productService.findByNameIgnoreCase(name, pageable);
        Page<ProductDTO> dtoPage = page.map(productMapper::productToProductDTO);
        PagedModel<EntityModel<ProductDTO>> model = assembler.toModel(dtoPage, productAssembler::toModel);
        return ResponseEntity.ok(model);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedModel<EntityModel<ProductDTO>>> search(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "minPrice", required = false) Double minPrice,
            @RequestParam(name = "maxPrice", required = false) Double maxPrice,
            Pageable pageable,
            PagedResourcesAssembler<ProductDTO> assembler
) {
    Page<Product> page = productService.search(q, category, minPrice, maxPrice, pageable);
    Page<ProductDTO> dtoPage = page.map(productMapper::productToProductDTO);
    PagedModel<EntityModel<ProductDTO>> model = assembler.toModel(dtoPage, productAssembler::toModel);
    
    return ResponseEntity.ok(model);
}

}