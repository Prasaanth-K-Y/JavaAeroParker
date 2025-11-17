package com.demo.ecommerce.repository;

import com.demo.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Spring Security will use this method to load the user
    Optional<User> findByUsername(String username);
}