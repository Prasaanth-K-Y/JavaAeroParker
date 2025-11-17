package com.demo.ecommerce.repository;

import com.demo.ecommerce.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    // Spring Data JPA will automatically implement all the
    // necessary database methods based on this interface.
    // We don't need to write any code here.
}