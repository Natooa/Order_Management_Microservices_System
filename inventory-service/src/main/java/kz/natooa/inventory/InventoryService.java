package kz.natooa.inventory;

public interface InventoryService {
    Inventory addInventory(String productId, Integer quantity);
    Inventory updateInventory(String productId, Integer newQuantity);
    void deleteInventory(String productId);
//    Integer getAvailableQuantity(String productId);
    void increaseReserved(String productId, Integer quantity);
    void decreaseReserved(String productId, Integer quantity);
    void commitReserved(String productId, Integer quantity);
}
