package com.makariev.examples.spring.bookstore.inventory;

import org.jmolecules.event.annotation.DomainEvent;

/**
 *
 * @author dmakariev
 */
@DomainEvent
public record StockRemovedEvent(Long inventoryId, int quantityRemoved) {

}
