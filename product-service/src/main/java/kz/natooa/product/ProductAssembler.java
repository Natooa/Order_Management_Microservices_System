package kz.natooa.product;

import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ProductAssembler implements RepresentationModelAssembler<ProductDTO, EntityModel<ProductDTO>> {

    @Override
    public EntityModel<ProductDTO> toModel(ProductDTO productDTO) {
        return EntityModel.of(productDTO,
                linkTo(methodOn(ProductController.class).getProductById(productDTO.getId())).withSelfRel(),
                linkTo(methodOn(ProductController.class).updateProduct(productDTO.getId(), productDTO)).withRel("update"),
                linkTo(methodOn(ProductController.class).deleteProduct(productDTO.getId())).withRel("delete")
        );
    }
}

