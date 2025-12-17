package com.demo.ecommerce.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PlaceOrderRequest(
    @NotNull(message = "Item ID cannot be null")
    @Positive(message = "Item ID must be a positive number")
    Long itemId,

    @Positive(message = "Quantity must be greater than 0")
    @Digits(integer = 10, fraction = 0, message = "Quantity must be a whole number without decimals")
    int quantity
) {}