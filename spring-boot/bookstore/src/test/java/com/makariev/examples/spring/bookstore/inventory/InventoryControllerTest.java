package com.makariev.examples.spring.bookstore.inventory;

import com.makariev.examples.spring.bookstore.product.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 *
 * @author dmakariev
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventoryService inventoryService;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setId(1L);
        inventory.setBook(new Book()); // Assuming there's a constructor or setter to set up a Book
        inventory.setQuantity(100);
    }

    @Test
    void getInventory_ShouldReturnInventory() throws Exception {
        given(inventoryService.findInventoryById(1L)).willReturn(inventory);

        mockMvc.perform(get("/api/inventory/{inventoryId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.quantity").value(100));
    }

    @Test
    void addStock_ShouldUpdateInventory() throws Exception {
        // Setup the expected behavior for adding stock
        Inventory updatedInventory = new Inventory(inventory.getId(), inventory.getBook(), inventory.getQuantity() + 10);
        given(inventoryService.addStock(1L, 10)).willReturn(updatedInventory);

        mockMvc.perform(post("/api/inventory/{inventoryId}/add", 1)
                .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(110)); // Expect updated quantity
    }

    @Test
    void removeStock_ShouldUpdateInventory() throws Exception {
        // Setup the expected behavior for removing stock
        Inventory updatedInventory = new Inventory(inventory.getId(), inventory.getBook(), inventory.getQuantity() - 5);
        given(inventoryService.removeStock(1L, 5)).willReturn(updatedInventory);

        mockMvc.perform(post("/api/inventory/{inventoryId}/remove", 1)
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(95)); // Expect updated quantity
    }
}
