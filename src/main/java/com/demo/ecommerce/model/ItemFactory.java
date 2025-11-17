package com.demo.ecommerce.model;

public class ItemFactory {

    public static Item create(String id, String name, int qty, double price) {
        return new Item(id, name, qty, price);
    }
}

