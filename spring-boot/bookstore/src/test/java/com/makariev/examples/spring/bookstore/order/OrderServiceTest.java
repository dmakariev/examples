package com.makariev.examples.spring.bookstore.order;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;

/**
 *
 * @author dmakariev
 */
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        // Additional setup can be done here
    }

    @Test
    void findAllOrders_ShouldReturnAllOrders() {
        final List<Order> expectedOrders = Arrays.asList(order);
        given(orderRepository.findAll()).willReturn(expectedOrders);

        final List<Order> orders = orderService.findAllOrders();

        assertThat(orders).hasSize(1);
        assertThat(orders.get(0)).isEqualTo(order);
    }

    @Test
    void findOrderById_ShouldReturnOrder() {
        given(orderRepository.findById(1L)).willReturn(Optional.of(order));

        final Optional<Order> foundOrder = orderService.findOrderById(1L);

        assertThat(foundOrder.isPresent()).isTrue();
        assertThat(foundOrder.get()).isEqualTo(order);
    }

    @Test
    void createOrUpdateOrder_ShouldSaveOrder() {
        given(orderRepository.save(order)).willReturn(order);

        final Order savedOrder = orderService.createOrUpdateOrder(order);

        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder).isEqualTo(order);
    }

    @Test
    void addOrderItem_ShouldAddItemToOrder() {
        final OrderItem orderItem = new OrderItem();
        orderItem.setId(2L);
        // Assuming setting up more properties if needed

        given(orderRepository.findById(1L)).willReturn(Optional.of(order));
        given(orderRepository.save(order)).willReturn(order);

        final Order updatedOrder = orderService.addOrderItem(1L, orderItem);

        assertThat(updatedOrder.getOrderItems()).contains(orderItem);
    }

    @Test
    void findOrderById_ShouldThrowExceptionIfNotFound() {
        given(orderRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.addOrderItem(1L, new OrderItem()));
    }

    @Test
    void deleteOrder_ShouldRemoveOrder() {
        willDoNothing().given(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        then(orderRepository).should().deleteById(1L);
    }
}
