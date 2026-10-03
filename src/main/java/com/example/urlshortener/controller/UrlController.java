package com.example.urlshortener.controller;

import com.example.urlshortener.dto.CreateUrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> createShortUrl(
            @Valid @RequestBody CreateUrlRequest request) {

        return ResponseEntity.ok(
                urlService.createShortUrl(request)
        );
    }
}