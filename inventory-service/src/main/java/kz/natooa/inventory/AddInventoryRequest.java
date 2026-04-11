package kz.natooa.inventory;

public record AddInventoryRequest (
        String productId,
        Integer quantity
){
}
