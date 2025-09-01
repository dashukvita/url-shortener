package com.urlshortener.controller

import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.dto.ResponseDto
import com.urlshortener.service.UrlShortener
import com.urlshortener.validation.UrlValidator.isNotValidUrl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(
    name = "URL Shortener API",
    description = "API for shortening long URLs and retrieving their original versions."
)
class UrlShortenerController(private val urlShortener: UrlShortener) {

    @PostMapping("/api/shorten")
    @Operation(summary = "Shorten a URL", description = "Returns a shortened version of the original URL")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "URL shortened successfully"),
        ApiResponse(responseCode = "400", description = "Invalid URL format")
    )
    fun shortenUrl(@RequestParam
                   @Parameter(description = "Original URL to shorten", example = "https://example.com")
                   originalUrl: String): ResponseEntity<ResponseDto> {
        if (isNotValidUrl(originalUrl)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseDto("Invalid url:$originalUrl"))
        }
        val shortUrl = urlShortener.shorten(originalUrl)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDto(shortUrl))
    }

    @GetMapping("/api/retrieve")
    @Operation(summary = "Retrieve original URL", description = "Returns the original URL for a given shortened URL")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Original URL found"),
        ApiResponse(responseCode = "404", description = "Short URL not found")
    )
    fun retrieveUrl(@RequestParam("shortUrl")
                    @Parameter(description = "Shortened URL", example = "https://short.ly/abc123")
                    shortUrl: String): ResponseEntity<ResponseDto> {
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