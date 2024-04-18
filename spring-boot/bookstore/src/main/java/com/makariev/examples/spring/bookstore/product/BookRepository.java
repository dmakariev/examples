package com.makariev.examples.spring.bookstore.product;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author dmakariev
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    // Fetching only necessary data using a JPQL query
    @Query("""
            SELECT new com.makariev.examples.spring.bookstore.product.BookSummary(
                b.title, 
                a.name, 
                b.price
            ) 
            FROM Book b JOIN b.author a
            """)
    List<BookSummary> findAllBookSummaries();
}
