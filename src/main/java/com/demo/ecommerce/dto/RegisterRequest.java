package com.demo.ecommerce.dto;

import com.demo.ecommerce.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "Name cannot be blank")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only alphabets and spaces")
        String name,

        @NotBlank(message = "Username cannot be blank")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only alphabets and numbers")
        String username,

        @NotBlank(message = "Password cannot be blank")
        String password,

        String address,

        @NotNull(message = "Role cannot be null")
        Role role
) {}
