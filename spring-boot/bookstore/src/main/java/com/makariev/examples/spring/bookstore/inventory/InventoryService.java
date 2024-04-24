package com.makariev.examples.spring.bookstore.inventory;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author dmakariev
 */
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public InventoryService(InventoryRepository inventoryRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.inventoryRepository = inventoryRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public Inventory findInventoryById(Long inventoryId) {
        return inventoryRepository.findById(inventoryId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Inventory not found for ID: " + inventoryId)
                );
    }

    @Transactional
    public Inventory addStock(Long inventoryId, int quantity) {
        final Inventory inventory = findInventoryById(inventoryId);
        inventory.addStock(quantity);
        final Inventory saved = inventoryRepository.save(inventory);
        applicationEventPublisher.publishEvent(new StockAddedEvent(inventoryId, quantity));
        return saved;
    }

    @Transactional
    public Inventory removeStock(Long inventoryId, int quantity) {
        final Inventory inventory = findInventoryById(inventoryId);
        inventory.removeStock(quantity);
        final Inventory saved = inventoryRepository.save(inventory);
        applicationEventPublisher.publishEvent(new StockRemovedEvent(inventoryId, quantity));
        return saved;
    }
}
