package com.makariev.examples.spring.bookstore.product;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author dmakariev
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {
    // Custom queries can be added here if needed
}
