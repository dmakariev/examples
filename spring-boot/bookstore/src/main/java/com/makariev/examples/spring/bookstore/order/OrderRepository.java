package com.makariev.examples.spring.bookstore.order;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author dmakariev
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Additional custom queries can be added here
}
