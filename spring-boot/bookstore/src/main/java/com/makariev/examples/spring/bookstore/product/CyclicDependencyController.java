//package com.makariev.examples.spring.bookstore.product;
//
//import com.makariev.examples.spring.bookstore.inventory.Inventory;
//import com.makariev.examples.spring.bookstore.inventory.InventoryService;
//import java.util.List;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/cycle")
//public class CyclicDependencyController {
//
//    private final InventoryService inventoryService;
//
//    public CyclicDependencyController(InventoryService inventoryService) {
//        this.inventoryService = inventoryService;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Inventory>> getAllInventories() {
//        final List<Inventory> inventories = inventoryService.findAll();
//        return ResponseEntity.ok(inventories);
//    }
//}
