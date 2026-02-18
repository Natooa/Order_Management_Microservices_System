package kz.natooa.product;

import kz.natooa.common.dto.PagedResponse;
import kz.natooa.common.exception.ProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import org.springframework.hateoas.PagedModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService{
    private final ProductRepository productRepository;
    private final MongoTemplate mongoTemplate;
    private final ProductMapper productMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, MongoTemplate mongoTemplate, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.mongoTemplate = mongoTemplate;
        this.productMapper = productMapper;
    }

    @Override
    public Product addProduct(Product product) {
        log.info("Adding product: {}", product);
        if(product == null) {
            throw new IllegalArgumentException("Product can't be null");
        }
        log.info("Product successfully added: {}", product);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String id, ProductDTO dto) {
        log.info("Updating product: {}", dto);
        Product oldProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        productMapper.updateEntityFromDto(dto, oldProduct);
        log.info("Product successfully updated: {}", oldProduct);

        return productRepository.save(oldProduct);
    }

    @Override
    public void removeProduct(String id) {
        log.info("Removing product with id: {}", id);
        if(!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product with ID: " + id + " not found");
        }
        productRepository.deleteById(id);
        log.info("Product successfully removed");
    }

    @Override
    public Product getProductById(String id) {
        log.info("Getting product with id: {}", id);
        return notFound(() -> productRepository.findById(id), "Product with ID: " + id + " not found");
    }

    @Override
    public Page<Product> findByNameIgnoreCase(String name, Pageable pageable) {
        log.info("Finding products by name: {}", name);
        Page<Product> products = productRepository.findByNameIgnoreCase(name, pageable);
        return products;
    }

    @Override
    public Page<Product> search(String q, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        log.info("Searching products: q={}, category={}, minPrice={}, maxPrice={}", q, category, minPrice, maxPrice);
        List<Criteria> criteriaList = new ArrayList<>();

        Query query = new Query();

        if (q != null && !q.isBlank()) {
            query.addCriteria(TextCriteria.forDefaultLanguage().matching(q));
        }

        if (category != null && !category.isBlank()) {
            criteriaList.add(Criteria.where("category").is(category));
        }
        if (minPrice != null || maxPrice != null){
            Criteria price= new Criteria("price");

            if(minPrice != null) price = price.gte(minPrice);
            if(maxPrice != null) price = price.lte(maxPrice);

            criteriaList.add(price);
        }

        if(!criteriaList.isEmpty()){
            query.addCriteria(new Criteria().andOperator(criteriaList));
        }

        long total = mongoTemplate.count(query, Product.class);

        List<Product> products = mongoTemplate.find(query.with(pageable), Product.class);

        Page<Product> page = new PageImpl<>(products, pageable, total);

        return page;
    }

    @Override
    public PagedResponse<ProductDTO> response (List<ProductDTO> dtoList, Page<Product> page){
        return new PagedResponse<>(
                dtoList,
                new PagedResponse.PageInfo(
                        page.getNumber() + 1,
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }

    private Product notFound(Supplier<Optional<Product>> supplier, String message){
        return supplier.get().orElseThrow(() -> new ProductNotFoundException(message));
    }
}