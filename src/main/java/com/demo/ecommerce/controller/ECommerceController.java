package com.demo.ecommerce.controller;


import com.demo.ecommerce.dto.AddItemRequest;
import com.demo.ecommerce.dto.PlaceOrderRequest;
import com.demo.ecommerce.dto.SimpleApiResponse;
import com.demo.ecommerce.model.Item;
import com.demo.ecommerce.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
@RequestMapping("/api")
@Validated
public class ECommerceController {

    private final ItemService itemService;

    public ECommerceController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * ENDPOINT 1: Add a new item to the inventory.
     * POST http://localhost:8080/api/items
     * Body: { "itemName": "Laptop", "quantity": 10, "price": 1200.0 }
     */
    @PostMapping("/items")
    public ResponseEntity<Item> addNewItem(@Valid @RequestBody Item newItem) {
        Item savedItem = itemService.addNewItem(newItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    /**
     * ENDPOINT 2: Place an order for an item.
     * POST http://localhost:8080/api/orders
     * Body: { "itemId": 123, "quantity": 2 }
     */
    @PostMapping("/orders")
    public ResponseEntity<SimpleApiResponse> placeOrder(@Valid @RequestBody PlaceOrderRequest orderRequest) {
        // The service handles all logic and throws exceptions if something goes wrong
        Item updatedItem = itemService.placeOrder(orderRequest);

        String message = "Order placed successfully! New stock for " + updatedItem.getItemName() + " is " + updatedItem.getQuantity();
        return ResponseEntity.ok(new SimpleApiResponse(message));
    }

    /**
     * ENDPOINT 3: Get an item's details.
     * GET http://localhost:8080/api/items/123
     */
    @GetMapping("/items/{itemId}")
    public ResponseEntity<Item> getItemById(
            @PathVariable @Positive(message = "Item ID must be a positive number") Long itemId) {

        Item item = itemService.getItemById(itemId);
        return ResponseEntity.ok(item);
    }
    /**
     * ENDPOINT 4: Get the headphone image (Streamed via Servlet).
     * GET http://localhost:8080/api/items/123/image
     */
    @GetMapping("/items/{itemId}/image")
    public void getItemImage(
            @PathVariable @Positive(message = "Item ID must be a positive number") Long itemId,
            HttpServletResponse response) throws IOException {

        String imagePath = "C:\\Users\\Pky\\Desktop\\temp\\bluetoothheadphones.jpg";
        File imgFile = new File(imagePath);

        if (!imgFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image not found");
            return;
        }

        response.setContentType("image/jpeg");
        response.setContentLengthLong(imgFile.length());

        try (InputStream in = new FileInputStream(imgFile);
             OutputStream out = response.getOutputStream()) {

            in.transferTo(out); // Java 9+ (cleaner)
        }
    }

}
