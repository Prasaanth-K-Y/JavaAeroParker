package com.demo.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
    @NotBlank(message = "Access token is required")
    String accessToken,

    String refreshToken  // Optional - can be null
) {
}
