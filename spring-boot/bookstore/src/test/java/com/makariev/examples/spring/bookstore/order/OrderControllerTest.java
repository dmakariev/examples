package com.makariev.examples.spring.bookstore.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.makariev.examples.spring.bookstore.product.Book;
import com.makariev.examples.spring.bookstore.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 *
 * @author dmakariev
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        final User customer = new User();  // Assuming User has a default constructor
        customer.setId(1L);

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setOrderDate(new Date());
        order.setTotalPrice(new BigDecimal("100.00"));
        order.setOrderItems(new HashSet<>());
    }

    @Test
    void getAllOrders_ShouldReturnAllOrders() throws Exception {
        final List<Order> orders = Arrays.asList(order);
        given(orderService.findAllOrders()).willReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(order.getId()));
    }

    @Test
    void getOrderById_ShouldReturnOrder() throws Exception {
        given(orderService.findOrderById(1L)).willReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/{orderId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()));
    }

    @Test
    void createOrder_ShouldCreateOrder() throws Exception {
        given(orderService.createOrUpdateOrder(any(Order.class))).willReturn(order);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()));
    }

    @Test
    void updateOrder_ShouldUpdateOrder() throws Exception {
        given(orderService.createOrUpdateOrder(any(Order.class))).willReturn(order);

        mockMvc.perform(put("/api/orders/{orderId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()));
    }

    @Test
    void addOrderItem_ShouldAddOrderItem() throws Exception {
        final OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setBook(new Book()); // Assuming Book has a default constructor
        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("50.00"));

        given(orderService.addOrderItem(eq(1L), any(OrderItem.class))).willReturn(order);

        mockMvc.perform(post("/api/orders/{orderId}/items", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(order.getId()));
    }

    @Test
    void deleteOrder_ShouldDeleteOrder() throws Exception {
        willDoNothing().given(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/orders/{orderId}", 1))
                .andExpect(status().isOk());
    }
}
