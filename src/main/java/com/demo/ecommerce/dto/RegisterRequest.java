package com.demo.ecommerce.dto;
import com.demo.ecommerce.model.Role;
// Using records for immutable DTOs
public record RegisterRequest(
        String name,
        String username,
        String password,
        String address,
        Role role
) {}
