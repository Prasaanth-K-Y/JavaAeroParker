package com.demo.ecommerce.dao;

import com.demo.ecommerce.model.Item;

public interface ItemDao {
    Item findById(String id);
    boolean exists(String id);
    Item save(Item item);
    Item update(Item item);

    java.util.List<Item> findAll();

}
