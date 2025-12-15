package com.demo.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlaceOrderRequest(
    @NotNull(message = "Item ID cannot be null")
    @Positive(message = "Item ID must be a positive number")
    Long itemId,

    @Positive(message = "Quantity must be greater than 0")
    int quantity
) {}