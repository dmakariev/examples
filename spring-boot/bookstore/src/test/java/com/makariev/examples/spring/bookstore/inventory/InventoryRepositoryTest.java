package com.makariev.examples.spring.bookstore.inventory;

import com.makariev.examples.spring.bookstore.product.Author;
import com.makariev.examples.spring.bookstore.product.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author dmakariev
 */
@DataJpaTest
@ActiveProfiles("test")
public class InventoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Test
    public void whenSaveInventory_thenFindById() {
        // Create and set up a new Author and Book for the test
        Author author = new Author();
        author.setName("Jane Austen");
        entityManager.persist(author);

        Book book = new Book();
        book.setTitle("Pride and Prejudice");
        book.setIsbn("1234567890123");
        book.setPrice(new BigDecimal("19.99"));
        book.setAuthor(author);
        entityManager.persist(book);

        // Create and save the inventory
        Inventory inventory = new Inventory();
        inventory.setBook(book);
        inventory.setQuantity(150);
        inventory = inventoryRepository.save(inventory);

        // Flush to database and clear the persistence context
        entityManager.flush();
        entityManager.clear();

        // Retrieve the inventory
        Inventory found = inventoryRepository.findById(inventory.getId()).orElse(null);

        // Assert the state of the retrieved inventory
        assertThat(found).isNotNull();
        assertThat(found.getQuantity()).isEqualTo(150);
        assertThat(found.getBook().getTitle()).isEqualTo("Pride and Prejudice");
    }

    @Test
    public void whenDeleteInventory_thenCannotFindById() {
        // Create and set up a new Author and Book for the test
        Author author = new Author();
        author.setName("Charles Dickens");
        entityManager.persist(author);

        Book book = new Book();
        book.setTitle("Great Expectations");
        book.setIsbn("9876543210123");
        book.setPrice(new BigDecimal("15.99"));
        book.setAuthor(author);
        entityManager.persist(book);

        // Create and save the inventory
        Inventory inventory = new Inventory();
        inventory.setBook(book);
        inventory.setQuantity(100);
        inventory = inventoryRepository.save(inventory);

        // Delete the inventory
        inventoryRepository.delete(inventory);

        // Flush to database and clear the persistence context
        entityManager.flush();
        entityManager.clear();

        // Assert that the inventory no longer exists
        Inventory found = inventoryRepository.findById(inventory.getId()).orElse(null);
        assertThat(found).isNull();
    }
}
