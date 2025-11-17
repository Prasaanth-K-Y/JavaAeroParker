package com.demo.ecommerce.controller;


import com.demo.ecommerce.dto.PlaceOrderRequest;
import com.demo.ecommerce.dto.SimpleApiResponse;
import com.demo.ecommerce.model.Item;
import com.demo.ecommerce.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api") // All endpoints will be prefixed with /api
public class ECommerceController {

    private final ItemService itemService;

    @Autowired
    public ECommerceController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * ENDPOINT 1: Add a new item to the inventory.
     * POST http://localhost:8080/api/items
     * Body: { "itemId": "A123", "itemName": "Laptop", "quantity": 10 }
     */
    @PostMapping("/items")
    public ResponseEntity<Item> addNewItem(@RequestBody Item newItem) {
        Item savedItem = itemService.addNewItem(newItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    /**
     * ENDPOINT 2: Place an order for an item.
     * POST http://localhost:8080/api/orders
     * Body: { "itemId": "A123", "quantity": 2 }
     */
    @PostMapping("/orders")
    public ResponseEntity<SimpleApiResponse> placeOrder(@RequestBody PlaceOrderRequest orderRequest) {
        // The service handles all logic and throws exceptions if something goes wrong
        Item updatedItem = itemService.placeOrder(orderRequest);
        
        String message = "Order placed successfully! New stock for " + updatedItem.getItemName() + " is " + updatedItem.getQuantity();
        return ResponseEntity.ok(new SimpleApiResponse(message));
    }

    /**
     * ENDPOINT 3: Get an item's details.
     * GET http://localhost:8080/api/items/A123
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable String itemId) {
        Item item = itemService.getItemById(itemId);
        return ResponseEntity.ok(item);
    }
}
