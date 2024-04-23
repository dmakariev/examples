package com.makariev.examples.spring.bookstore.inventory;

/**
 *
 * @author dmakariev
 */
public record StockRemovedEvent(Long inventoryId, int quantityRemoved) {

}
