package com.demo.ecommerce.dao;

import com.demo.ecommerce.model.Item;
import java.util.List;

public interface ItemDao {

    boolean exists(Long id);

    Item findById(Long id);

    Item findByName(String itemName);

    Item save(Item item);

    Item update(Item item);

    List<Item> findAll();
}
