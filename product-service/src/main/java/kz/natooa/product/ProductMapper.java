package kz.natooa.product;

import kz.natooa.common.dto.PagedResponse;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductDTO productToProductDTO(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductDTO dto, @MappingTarget Product product);

    @Mapping(target = "id", ignore = true)
    Product productDTOToProduct(ProductDTO productDTO);

    List<ProductDTO> productPageToProductDTOList(Page<Product> page);
}

