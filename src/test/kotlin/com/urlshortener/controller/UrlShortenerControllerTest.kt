package com.urlshortener.controller

import com.urlshortener.model.UrlDocument
import com.urlshortener.repository.StorageRepository
import com.urlshortener.service.UrlShortener
import com.urlshortener.service.UrlShortenerImpl
import com.urlshortener.service.hashGenerator.HashGenerator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Instant

class UrlShortenerControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var urlShortener: UrlShortener
    private val mockGenerator: HashGenerator = mock()
    private val mockStorage: StorageRepository = mock()

    @BeforeEach
    fun setup() {
        urlShortener = UrlShortenerImpl(
            generator = mockGenerator,
            storageRepository = mockStorage
        )

        mockMvc = MockMvcBuilders
            .standaloneSetup(UrlShortenerController(urlShortener))
            .build()

        whenever(mockGenerator.encode("https://example.com")).thenReturn("abc123")
    }

    @Test
    fun `POST shortenUrl should return CREATED with short URL for valid URL`() {
        val originalUrl = "https://example.com"
        val shortCode = "abc123"
        val shortUrl = "https://short.ly/$shortCode"

        whenever(mockStorage.findUrlDocumentByLongUrl(originalUrl)).thenReturn(null)
        whenever(mockStorage.save(UrlDocument(shortUrl = shortCode, longUrl = originalUrl, createdAt = Instant.now())))
            .thenAnswer {}

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
        val shortCode = "abc123"
        val shortUrl = "https://short.ly/$shortCode"
        val originalUrl = "https://example.com"

        whenever(mockStorage.findUrlDocumentByShortUrl(shortCode))
            .thenReturn(UrlDocument(shortUrl = shortCode, longUrl = originalUrl, createdAt = Instant.now()))

        mockMvc.perform(
            get("/api/retrieve")
                .param("shortUrl", shortUrl)
        )
            .andExpect(status().isOk)
            .andExpect(content().string(originalUrl))
    }

    @Test
    fun `GET retrieveUrl should return NOT_FOUND when short URL not found`() {
        val shortUrl = "https://short.ly/unknown"
        val shortCode = "unknown"

        whenever(mockStorage.findUrlDocumentByShortUrl(shortCode)).thenReturn(null)

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