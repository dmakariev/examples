package com.makariev.examples.spring.bookstore.user;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author dmakariev
 */
public interface UserRepository extends JpaRepository<User, Long> {
    // You can add custom queries here if needed, for example, to search by attributes specific to users or customers.
}
