package com.urlshortener.service

import com.urlshortener.constants.Constants.DOMAIN
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UrlShortenerImplTest {
    private lateinit var urlShortener: UrlShortenerImpl

    @BeforeEach
    fun setUp() {
        urlShortener = UrlShortenerImpl()
    }

    @Test
    fun `shorten should return a short URL starting with domain`() {
        val originalUrl = "https://example.com"
        val shortUrl = urlShortener.shorten(originalUrl)

        assertNotNull(shortUrl)
        assertTrue(shortUrl.startsWith(DOMAIN))
    }

    @Test
    fun `shorten should return the same short URL for the same original URL`() {
        val originalUrl = "https://example.com"
        val shortUrl1 = urlShortener.shorten(originalUrl)
        val shortUrl2 = urlShortener.shorten(originalUrl)

        assertEquals(shortUrl1, shortUrl2)
    }

    @Test
    fun `retrieve should return original URL for given shortURL`() {
        val originalUrl = "https://example.com"
        val shortUrl = urlShortener.shorten(originalUrl)

        val retrieved = urlShortener.retrieve(shortUrl)
        assertEquals(originalUrl, retrieved)
    }

    @Test
    fun `retrieve should return null for unknown URL`() {
        val unknownShortUrl = DOMAIN + "abcd"
        val retrieved = urlShortener.retrieve(unknownShortUrl)

        assertNull(retrieved)
    }

    @Test
    fun `shorten should generate unique codes for different URLs`() {
        val url1 = "https://example.com/1"
        val url2 = "https://example.com/2"

        val short1 = urlShortener.shorten(url1)
        val short2 = urlShortener.shorten(url2)

        assertNotEquals(short1, short2)
    }
}