package com.makariev.examples.spring.bookstore.inventory;

import com.makariev.examples.spring.bookstore.product.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

/**
 *
 * @author dmakariev
 */
@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

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
    void findInventoryById_ShouldReturnInventory() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(inventory));

        Inventory found = inventoryService.findInventoryById(1L);

        assertThat(found).isEqualTo(inventory);
    }

    @Test
    void findInventoryById_ShouldThrowExceptionIfNotFound() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventoryService.findInventoryById(1L));
    }

    @Test
    void addStock_ShouldIncreaseInventory() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(inventory));
        inventory.addStock(10); // Expect this method to increase quantity to 110
        given(inventoryRepository.save(inventory)).willReturn(inventory);

        Inventory updatedInventory = inventoryService.addStock(1L, 10);

        assertThat(updatedInventory.getQuantity()).isEqualTo(120);
    }

    @Test
    void removeStock_ShouldDecreaseInventory() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(inventory));
        inventory.removeStock(20); // Expect this method to decrease quantity to 80
        given(inventoryRepository.save(inventory)).willReturn(inventory);

        Inventory updatedInventory = inventoryService.removeStock(1L, 20);

        assertThat(updatedInventory.getQuantity()).isEqualTo(60);
    }

    @Test
    void removeStock_ShouldThrowExceptionIfNotEnoughStock() {
        given(inventoryRepository.findById(1L)).willReturn(Optional.of(inventory));

        assertThrows(IllegalArgumentException.class, () -> inventoryService.removeStock(1L, 150));
    }
}
