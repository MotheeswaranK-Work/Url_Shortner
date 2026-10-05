package com.example.urlshortener.service;

import com.example.urlshortener.dto.CreateUrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.entity.UrlMapping;
import com.example.urlshortener.exception.UrlExpiredException;
import com.example.urlshortener.exception.UrlNotFoundException;
import com.example.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    + "abcdefghijklmnopqrstuvwxyz"
                    + "0123456789";

    private static final int CODE_LENGTH = 6;

    private final Random random = new Random();

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }



    public UrlResponse createShortUrl(
            CreateUrlRequest request) {

        String shortCode = generateUniqueCode();

        LocalDateTime expiresAt = null;

        if (request.expiresAt() != null
                && !request.expiresAt().isBlank()) {

            expiresAt =
                    LocalDateTime.parse(request.expiresAt());
        }

        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl(request.originalUrl())
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .clickCount(0L)
                .build();

        UrlMapping saved =
                urlRepository.save(urlMapping);

        return convertToResponse(saved);
    }

    private String generateUniqueCode() {

        String code;

        do {

            code = generateRandomCode();

        } while (urlRepository.existsByShortCode(code));

        return code;
    }

    private String generateRandomCode() {

        StringBuilder code = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {

            int index =
                    random.nextInt(CHARACTERS.length());

            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    @Transactional
    public String redirect(String shortCode) {

        UrlMapping urlMapping =
                urlRepository.findByShortCode(shortCode)
                        .orElseThrow(() ->
                                new UrlNotFoundException(
                                        "Short URL not found"
                                ));

        if (urlMapping.getExpiresAt() != null
                && LocalDateTime.now()
                .isAfter(urlMapping.getExpiresAt())) {

            throw new UrlExpiredException(
                    "Short URL has expired"
            );
        }

        urlMapping.setClickCount(
                urlMapping.getClickCount() + 1
        );

        urlRepository.save(urlMapping);

        return urlMapping.getOriginalUrl();
    }

    private UrlResponse convertToResponse(
            UrlMapping urlMapping) {

        String shortUrl ="http://localhost:8080/r/"+ urlMapping.getShortCode();
        //String shortUrl = "http://localhost:8080/r/" + saved.getShortCode();

        return new UrlResponse(
                urlMapping.getId(),
                urlMapping.getOriginalUrl(),
                urlMapping.getShortCode(),
                shortUrl,
                urlMapping.getCreatedAt(),
                urlMapping.getExpiresAt(),
                urlMapping.getClickCount()
        );
    }
}