package com.makariev.examples.spring.bookstore.inventory;

/**
 *
 * @author dmakariev
 */
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public Inventory findInventoryById(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for ID: " + inventoryId));
    }

    @Transactional
    public Inventory addStock(Long inventoryId, int quantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found!"));
        inventory.addStock(quantity);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory removeStock(Long inventoryId, int quantity) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("Inventory not found!"));
        inventory.removeStock(quantity);
        return inventoryRepository.save(inventory);
    }
}
