package kz.natooa.product;

import org.mapstruct.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    ProductDTO productToProductDTO(Product product);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductDTO dto, @MappingTarget Product product);

    @Mapping(target = "id", ignore = true)
    Product productDTOToProduct(ProductDTO productDTO);
}

