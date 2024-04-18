package com.makariev.examples.spring.bookstore.product;

import java.math.BigDecimal;

/**
 *
 * @author dmakariev
 */
public record BookSummary(String title, String authorName, double price) {

    public BookSummary(String title, String authorName, BigDecimal price) {
        this(title, authorName, price.doubleValue());
    }

}
