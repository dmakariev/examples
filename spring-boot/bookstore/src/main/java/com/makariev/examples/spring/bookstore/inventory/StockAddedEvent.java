package com.makariev.examples.spring.bookstore.inventory;

import org.jmolecules.event.annotation.DomainEvent;

/**
 *
 * @author dmakariev
 */
@DomainEvent
public record StockAddedEvent(Long inventoryId, int quantityAdded) {

}
