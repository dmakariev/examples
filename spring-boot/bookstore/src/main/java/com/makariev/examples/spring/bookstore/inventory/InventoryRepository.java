package com.makariev.examples.spring.bookstore.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author dmakariev
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Additional custom queries can be added here
}
