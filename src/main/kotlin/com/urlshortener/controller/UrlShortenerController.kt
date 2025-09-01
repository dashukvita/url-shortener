package com.urlshortener.controller

import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.dto.ResponseDto
import com.urlshortener.service.UrlShortener
import com.urlshortener.validation.UrlValidator.isNotValidUrl
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class UrlShortenerController(private val urlShortener: UrlShortener) {

    @PostMapping("/api/shorten")
    fun shortenUrl(@RequestParam originalUrl: String): ResponseEntity<ResponseDto> {
        if (isNotValidUrl(originalUrl)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto("Invalid url:$originalUrl"))
        }
        val shortUrl = urlShortener.shorten(originalUrl)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDto(shortUrl))
    }

    @GetMapping("/api/retrieve")
    fun retrieveUrl(@RequestParam("shortUrl") shortUrl: String): ResponseEntity<ResponseDto> {
        if (!shortUrl.startsWith(DOMAIN)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto("Invalid url:$shortUrl"))
        }

        val originalUrl = urlShortener.retrieve(shortUrl)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ResponseDto("Short URL not found"))

        return ResponseEntity.ok(ResponseDto(originalUrl))
    }
}