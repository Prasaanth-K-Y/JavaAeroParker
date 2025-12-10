package com.demo.ecommerce.controller;


import com.demo.ecommerce.dto.PlaceOrderRequest;
import com.demo.ecommerce.dto.SimpleApiResponse;
import com.demo.ecommerce.model.Item;
import com.demo.ecommerce.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse; // The "Servlet" part
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.springframework.util.StreamUtils; // Optional helper, or use standard Java
@RestController
@RequestMapping("/api")//CDI 
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
    /**
     * ENDPOINT 4: Get the headphone image (Streamed via Servlet).
     * GET http://localhost:8080/api/items/A123/image
     */
    @GetMapping("/items/{itemId}/image")
    public void getItemImage(@PathVariable String itemId, HttpServletResponse response) throws IOException {
        
        // 1. DEFINE IMAGE PATH
        // Note: We use double backslashes "\\" to escape the path in Java
        // Logic: You could switch the file based on the itemId here if needed
        String imagePath = "C:\\Users\\Pky\\Desktop\\temp\\bluetoothheadphones.jpg";
        File imgFile = new File(imagePath);

        // 2. CHECK IF EXISTS
        if (!imgFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Image file not found at: " + imagePath);
            return;
        }

        // 3. SET HEADERS
        response.setContentType("image/jpeg"); // Changed to jpeg since your file is .jpg
        response.setContentLength((int) imgFile.length());

        // 4. STREAM THE BYTES
        try (InputStream in = new FileInputStream(imgFile);
             OutputStream out = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096]; // 4KB buffer
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
