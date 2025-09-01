package com.urlshortener.controller

import com.urlshortener.service.UrlShortener
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(UrlShortenerController::class)
class UrlShortenerControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val urlShortener: UrlShortener
) {

    @TestConfiguration
    class MockConfig {
        @Bean
        fun urlShortener(): UrlShortener = mock()
    }

    @Test
    fun `POST shortenUrl should return CREATED with short URL for valid URL`() {
        val originalUrl = "https://example.com"
        val shortUrl = "https://short.ly/abc123"

        whenever(urlShortener.shorten(originalUrl)).thenReturn(shortUrl)

        mockMvc.perform(
            post("/api/shorten")
                .contentType(MediaType.TEXT_PLAIN)
                .content(originalUrl)
        )
            .andExpect(status().isCreated)
            .andExpect(content().string(shortUrl))
    }

    @Test
    fun `POST shortenUrl should return BAD_REQUEST for invalid URL`() {
        val invalidUrl = "invalid-url"

        mockMvc.perform(
            post("/api/shorten")
                .contentType(MediaType.TEXT_PLAIN)
                .content(invalidUrl)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Invalid url:$invalidUrl"))
    }

    @Test
    fun `GET retrieveUrl should return original URL when found for valid URL`() {
        val shortUrl = "https://short.ly/abc123"
        val originalUrl = "https://example.com"

        whenever(urlShortener.retrieve(shortUrl)).thenReturn(originalUrl)

        mockMvc.perform(
            get("/api/retrieve")
                .param("shortUrl", shortUrl)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(originalUrl))
    }

    @Test
    fun `GET retrieveUrl should return NOT_FOUND when short URL not found for valid URL`() {
        val shortUrl = "https://short.ly/unknown"

        whenever(urlShortener.retrieve(shortUrl)).thenReturn(null)

        mockMvc.perform(
            get("/api/retrieve")
                .param("shortUrl", shortUrl)
        )
            .andExpect(status().isNotFound)
            .andExpect(content().string("Short URL not found"))
    }

    @Test
    fun `GET retrieveUrl should return BAD_REQUEST when short URL has invalid domain`() {
        val invalidShortUrl = "https://wrong.ly/abc123"

        mockMvc.perform(
            get("/api/retrieve")
                .param("shortUrl", invalidShortUrl)
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string("Invalid url:$invalidShortUrl"))
    }
}