package kz.natooa.orderItems;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "priceAtPurchase", ignore = true)
    @Mapping(target = "order", ignore = true)  // добавь это
    @Mapping(target = "id", ignore = true)      // и это
    OrderItem toEntity(OrderItemRequestDTO dto);

    List<OrderItem> toEntityList(List<OrderItemRequestDTO> dtos);
}