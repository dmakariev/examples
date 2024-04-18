package com.makariev.examples.spring.bookstore.inventory;

import com.makariev.examples.spring.bookstore.product.Book;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author dmakariev
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Find inventory by book
    Inventory findByBook(Book book);
}
