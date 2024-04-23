package com.makariev.examples.spring.bookstore.notification;

import com.makariev.examples.spring.bookstore.inventory.StockAddedEvent;
import com.makariev.examples.spring.bookstore.inventory.StockRemovedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 *
 * @author dmakariev
 */
@Service
public class NotificationService {

    @EventListener
    public void onStockAdded(StockAddedEvent event) {
        // Handle stock addition (logging, notifications, etc.)
        System.out.println("Stock added: " + event.quantityAdded() + " to inventory ID: " + event.inventoryId());
    }

    @EventListener
    public void onStockRemoved(StockRemovedEvent event) {
        // Handle stock removal (logging, notifications, etc.)
        System.out.println("Stock removed: " + event.quantityRemoved() + " from inventory ID: " + event.inventoryId());
    }

}
