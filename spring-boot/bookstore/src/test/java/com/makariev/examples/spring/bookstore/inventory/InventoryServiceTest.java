package com.makariev.examples.spring.bookstore.inventory;

import com.makariev.examples.spring.bookstore.product.Book;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.ArgumentCaptor;
import static org.mockito.BDDMockito.given;
import org.mockito.Captor;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.context.ApplicationEventPublisher;

/**
 *
 * @author dmakariev
 */
@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Captor
    private ArgumentCaptor<StockAddedEvent> stockAddedEventCaptor;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setBook(new Book()); // Assume Book setup
        inventory.setQuantity(100);
    }

    @Test
    void findAllInventories_ShouldReturnAllInventories() {
        final List<Inventory> expectedInventories = Arrays.asList(inventory);
        given(inventoryRepository.findAll()).willReturn(expectedInventories);

        final List<Inventory> inventories = inventoryService.findAll();

        assertThat(inventories).hasSize(1);
        assertThat(inventories.get(0)).isEqualTo(inventory);
    }

    @Test
    void findInventoryById_ShouldReturnInventory() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(inventory));

        final Inventory found = inventoryService.findInventoryById(1L);

        assertThat(found).isEqualTo(inventory);
    }

    @Test
    void findInventoryById_ShouldThrowExceptionIfNotFound() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventoryService.findInventoryById(1L));
    }

    @Test
    void addStock_ShouldIncreaseInventoryAndPublishEvent() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(inventory));
        given(inventoryRepository.save(inventory)).willReturn(inventory);

        final Inventory updatedInventory = inventoryService.addStock(1L, 10);

        assertThat(updatedInventory.getQuantity()).isEqualTo(110); // assuming the initial setup is corrected
        verify(applicationEventPublisher, times(1)).publishEvent(new StockAddedEvent(1L, 10));

        //use an ArgumentCaptor to capture and assert properties of events:
        verify(applicationEventPublisher).publishEvent(stockAddedEventCaptor.capture());
        final StockAddedEvent capturedEvent = stockAddedEventCaptor.getValue();
        assertThat(capturedEvent.inventoryId()).isEqualTo(1L);
        assertThat(capturedEvent.quantityAdded()).isEqualTo(10);
    }

    @Test
    void removeStock_ShouldDecreaseInventoryAndPublishEvent() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(inventory));
        given(inventoryRepository.save(inventory)).willReturn(inventory);

        final Inventory updatedInventory = inventoryService.removeStock(1L, 20);

        assertThat(updatedInventory.getQuantity()).isEqualTo(80); // assuming the initial setup is corrected
        verify(applicationEventPublisher, times(1)).publishEvent(new StockRemovedEvent(1L, 20));
    }

    @Test
    void removeStock_ShouldThrowExceptionIfNotEnoughStock() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(inventory));

        assertThrows(IllegalArgumentException.class, () -> inventoryService.removeStock(1L, 150));
    }

}
