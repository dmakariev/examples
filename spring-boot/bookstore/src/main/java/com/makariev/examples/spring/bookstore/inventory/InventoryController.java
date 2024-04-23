package com.makariev.examples.spring.bookstore.inventory;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author dmakariev
 */
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventories() {
        final List<Inventory> inventories = inventoryService.findAll();
        return ResponseEntity.ok(inventories);
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<Inventory> getInventory(@PathVariable Long inventoryId) {
        final Inventory inventory = inventoryService.findInventoryById(inventoryId);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping("/{inventoryId}/add")
    public ResponseEntity<Inventory> addStock(@PathVariable Long inventoryId, @RequestParam int quantity) {
        final Inventory updatedInventory = inventoryService.addStock(inventoryId, quantity);
        return ResponseEntity.ok(updatedInventory);
    }

    @PostMapping("/{inventoryId}/remove")
    public ResponseEntity<Inventory> removeStock(@PathVariable Long inventoryId, @RequestParam int quantity) {
        final Inventory updatedInventory = inventoryService.removeStock(inventoryId, quantity);
        return ResponseEntity.ok(updatedInventory);
    }
}
