package com.demo.ecommerce.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record AddItemRequest(
    @NotBlank(message = "Item name cannot be blank")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Item name must contain only alphabets and spaces")
    String itemName,

    @Positive(message = "Quantity must be greater than 0")
    @Digits(integer = 10, fraction = 0, message = "Quantity must be a whole number without decimals")
    int quantity,

    @Positive(message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 0, message = "Price must be a whole number without decimals")
    int price
) {}
