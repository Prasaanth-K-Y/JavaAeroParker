package com.demo.ecommerce.service;


import com.demo.ecommerce.dto.PlaceOrderRequest;
import com.demo.ecommerce.exception.InsufficientStockException;
import com.demo.ecommerce.exception.ItemAlreadyExistsException;
import com.demo.ecommerce.exception.ItemNotFoundException;
import com.demo.ecommerce.model.Item;

import java.util.List;

public interface ItemService {

    /**
     * Adds a new item to the inventory.
     * @param newItem The item to add.
     * @return The saved item.
     * @throws ItemAlreadyExistsException if an item with the same ID already exists.
     * @throws IllegalArgumentException if item ID is null or blank.
     */
    Item addNewItem(Item newItem);

    /**
     * Places an order for a given item, reducing its stock.
     * @param orderRequest The request containing item ID and quantity.
     * @return The updated Item with new stock.
     * @throws ItemNotFoundException if the item doesn't exist.
     * @throws InsufficientStockException if the requested quantity is not available.
     */
    Item placeOrder(PlaceOrderRequest orderRequest);

    /**
     * Finds an item by its ID.
     * @param itemId The ID of the item to find.
     * @return The found item.
     * @throws ItemNotFoundException if the item doesn't exist.
     */
    Item getItemById(String itemId);

    /**
     * Finds an item by its ID.
     * @param itemId The ID of the item to find.
     * @return The found item.
     * @throws ItemNotFoundException if the item doesn't exist.
     */
    List<Item> getLowStockItems();

    /**
     * Finds all items.
     * @return A list of all items.
     */
    List<Item> findAll();
}

