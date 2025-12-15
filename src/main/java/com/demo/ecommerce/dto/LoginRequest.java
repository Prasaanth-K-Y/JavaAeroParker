package com.demo.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "Username cannot be blank")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only alphabets and numbers")
        String username,

        @NotBlank(message = "Password cannot be blank")
        String password
) {} 
    

