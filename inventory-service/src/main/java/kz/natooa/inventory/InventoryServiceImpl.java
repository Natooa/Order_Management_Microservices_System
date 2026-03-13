package kz.natooa.inventory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService{
    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    //CRUD operation for inventory

    @Override
    public Inventory addInventory(String productId, Integer quantity) {
        if(productId == null || quantity == null){
            throw new IllegalArgumentException("Product ID and quantity must not be null");
        }
        if(inventoryRepository.existsById(productId)){
            throw new IllegalArgumentException("Inventory for this product already exists");
        }

        Inventory inventory = new Inventory.InventoryBuilder()
                .productId(productId)
                .totalQuantity(quantity)
                .reservedQuantity(0)
                .build();
        return inventoryRepository.save(inventory);
    }


//Only update quantity
    @Override
    public Inventory updateInventory(String productId, Integer newQuantity) {
        if(productId == null || newQuantity == null){
            throw new IllegalArgumentException("Product ID and new quantity must not be null");
        }

        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId).orElseThrow(() -> new IllegalArgumentException("Product not found:  " + productId));

        if(newQuantity < inventory.getReservedQuantity()){
            throw new IllegalArgumentException("New quantity must be greater than or equal to the reserved quantity");
        }
        inventory.setTotalQuantity(newQuantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public void deleteInventory(String productId) {
        if(productId == null){
            throw new IllegalArgumentException("Product ID must not be null");
        }

        Inventory inventory = inventoryRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found:  " + productId));
        if(inventory.getReservedQuantity() > 0){
            throw new IllegalArgumentException("Cannot delete inventory for product with reserved quantity");
        }
        inventoryRepository.delete(inventory);
    }

//    @Override
//    public Integer getAvailableQuantity(String productId) {
//        if(productId == null) {
//            throw new IllegalArgumentException("Product ID must not be null");
//        }
//        Inventory inventory = inventoryRepository.findById(productId).orElseThrow();
//        return inventory.getTotalQuantity() - inventory.getReservedQuantity();
//    }


    //method of reservations
    @Transactional
    @Override
    public void increaseReserved(String productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        int availableQuantity = inventory.getTotalQuantity() - inventory.getReservedQuantity();
        if(quantity > availableQuantity){
            throw new IllegalArgumentException("Not enough inventory available for this reservation");
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    @Override
    public void decreaseReserved(String productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if(inventory.getReservedQuantity() < quantity){
            throw new IllegalArgumentException("Cannot cancel more than the reserved quantity");
        }
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    @Override
    public void commitReserved(String productId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Inventory inventory = inventoryRepository.findByProductIdForUpdate(productId).orElseThrow(() -> new IllegalArgumentException("Product not found"));
        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setTotalQuantity(inventory.getTotalQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
}
