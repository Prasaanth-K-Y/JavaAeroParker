package com.demo.ecommerce.controller;

import com.demo.ecommerce.dto.PlaceOrderRequest;
import com.demo.ecommerce.dto.SimpleApiResponse;
import com.demo.ecommerce.model.Item;
import com.demo.ecommerce.service.ItemService;
import com.demo.ecommerce.service.ItemServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ItemViewController {

    private final ItemService itemService;
    private final ItemServiceImpl itemServiceImpl;

    public ItemViewController(ItemService itemService,
                              @Qualifier("fastItemService") ItemServiceImpl itemServiceImpl) {
        this.itemService = itemService;
        this.itemServiceImpl = itemServiceImpl;
    }

    /**
     * View all items in JSP page
     * GET http://localhost:8080/items/view
     */
    @GetMapping("/items/view")
    public String viewItems(Model model) {
        List<Item> items = itemService.findAll();
        model.addAttribute("items", items);
        return "items";
    }

    /**
     * Place order from JSP page (public endpoint with gRPC notification)
     * POST http://localhost:8080/jsp/orders
     */
    @PostMapping("/jsp/orders")
    @ResponseBody
    public ResponseEntity<SimpleApiResponse> placeOrderFromJsp(@RequestBody PlaceOrderRequest orderRequest) {
        try {
            Item updatedItem = itemServiceImpl.processOrderWithLowStockCheck(
                orderRequest.itemId(),
                orderRequest.quantity()
            );

            String message = "Order placed successfully! New stock for " +
                           updatedItem.getItemName() + " is " + updatedItem.getQuantity();

            return ResponseEntity.ok(new SimpleApiResponse(message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                               .body(new SimpleApiResponse(e.getMessage()));
        }
    }
}
