package com.makariev.examples.spring.bookstore.inventory;

/**
 *
 * @author dmakariev
 */
public record StockAddedEvent(Long inventoryId, int quantityAdded) {

}
