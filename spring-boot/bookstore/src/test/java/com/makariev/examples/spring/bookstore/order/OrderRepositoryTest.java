package com.makariev.examples.spring.bookstore.order;

import com.makariev.examples.spring.bookstore.product.Author;
import com.makariev.examples.spring.bookstore.product.Book;
import com.makariev.examples.spring.bookstore.user.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.context.ActiveProfiles;

/**
 *
 * @author dmakariev
 */
@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void whenSaveOrder_thenFindById() {
        // Set up Author and Book
        Author author = new Author("John Tolkien");
        entityManager.persist(author);

        Book book = new Book();
        book.setTitle("The Lord of the Rings");
        book.setIsbn("1234567890");
        book.setPrice(new BigDecimal("29.99"));
        book.setAuthor(author);
        entityManager.persist(book);

        // Set up User and Order
        Customer customer = new Customer("john_doe", "password123", "john.doe@example.com", new Date());
        entityManager.persist(customer);

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(new Date());
        order.setTotalPrice(new BigDecimal("29.99"));

        OrderItem orderItem = new OrderItem();
        orderItem.setBook(book);
        orderItem.setQuantity(1);
        orderItem.setPrice(book.getPrice());
        orderItem.setOrder(order);
        order.getOrderItems().add(orderItem);

        entityManager.persist(order);
        entityManager.flush();  // Ensure all changes are flushed to the DB

        // Retrieve the order
        Optional<Order> found = orderRepository.findById(order.getId());
        assertThat(found.isPresent()).isTrue();
        assertThat(found.get().getOrderItems()).hasSize(1);
        assertThat(found.get().getCustomer().getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    public void whenFindAllOrders_thenGetOrders() {
        Customer customer = new Customer("alice_johnson", "securepass", "alice@example.com", new Date());
        entityManager.persist(customer);

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(new Date());
        order.setTotalPrice(new BigDecimal("100.00"));
        entityManager.persist(order);
        entityManager.flush();

        List<Order> orders = orderRepository.findAll();
        assertThat(orders).isNotEmpty();
        assertThat(orders.get(0).getCustomer()).isEqualTo(customer);
    }
}
