package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUrlRequest(

        @NotBlank(message = "URL is required")
        @Size(max = 2048)

        String originalUrl,

        String expiresAt
) {
}