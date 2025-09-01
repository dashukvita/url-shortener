package com.urlshortener.controller

import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.service.UrlShortener
import com.urlshortener.validation.UrlValidator.isNotValidUrl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UrlShortenerController(private val urlShortener: UrlShortener) {

    @PostMapping("/api/shorten")
    fun shortenUrl(@RequestBody originalUrl: String): ResponseEntity<String> {
        if (isNotValidUrl(originalUrl)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid url:$originalUrl")
        }
        val shortUrl = urlShortener.shorten(originalUrl)
        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrl)
    }

    @GetMapping("/api/retrieve")
    fun retrieveUrl(@RequestParam("shortUrl") shortUrl: String): ResponseEntity<String> {
        if (!shortUrl.startsWith(DOMAIN)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid url:$shortUrl")
        }

        val originalUrl = urlShortener.retrieve(shortUrl)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Short URL not found")

        return ResponseEntity.ok(originalUrl)
    }
}