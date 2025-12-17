package com.demo.ecommerce.model;

public class ItemFactory {

    public static Item create(String id, String name, int qty, int price) {
        return new Item( name, qty, price);
    }
}

