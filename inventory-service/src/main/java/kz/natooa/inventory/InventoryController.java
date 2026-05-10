package kz.natooa.inventory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @PostMapping("/addInventory")
    public ResponseEntity<Inventory> addInventory(@RequestBody AddInventoryRequest request){
        return ResponseEntity.ok(inventoryService.addInventory(request.productId(), request.quantity()));
    }
}
