package com.makariev.examples.spring.bookstore.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makariev.examples.spring.bookstore.product.Author;
import com.makariev.examples.spring.bookstore.product.Book;
import com.makariev.examples.spring.bookstore.user.Customer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 *
 * @author dmakariev
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;  // Using EntityManager instead of TestEntityManager

    @Autowired
    private PlatformTransactionManager transactionManager;  // Inject the PlatformTransactionManager

    @BeforeEach
    void setUp() {
        TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            entityManager.createQuery("DELETE FROM OrderItem").executeUpdate();
            entityManager.createQuery("DELETE FROM Order").executeUpdate();
            entityManager.createQuery("DELETE FROM Book").executeUpdate();
            entityManager.createQuery("DELETE FROM Author").executeUpdate();
            entityManager.createQuery("DELETE FROM User").executeUpdate();
            setupEntities();
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            throw e;
        }
    }

    private void setupEntities() {

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

    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].totalPrice").value("29.99"));
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        Order order = orderRepository.findAll().get(0);

        mockMvc.perform(get("/api/orders/{orderId}", order.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value("29.99"));
    }

    @Test
    void createOrder_ShouldCreateOrder() throws Exception {
        Order newOrder = new Order();
        newOrder.setCustomer(orderRepository.findAll().get(0).getCustomer());
        newOrder.setOrderDate(new Date());
        newOrder.setTotalPrice(new BigDecimal("200.00"));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value("200.0"));
    }

    @Test
    void updateOrder_ShouldUpdateOrder() throws Exception {
        Order existingOrder = orderRepository.findAll().get(0);
        existingOrder.setTotalPrice(new BigDecimal("500.00"));

        mockMvc.perform(put("/api/orders/{orderId}", existingOrder.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(existingOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPrice").value("500.0"));
    }

    @Test
    void deleteOrder_ShouldRemoveOrder() throws Exception {
        Order existingOrder = orderRepository.findAll().get(0);

        mockMvc.perform(delete("/api/orders/{orderId}", existingOrder.getId()))
                .andExpect(status().isOk());
    }
}
