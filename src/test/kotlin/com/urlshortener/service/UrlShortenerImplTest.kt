package com.urlshortener.service

import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.constants.Constants.TTL
import com.urlshortener.model.UrlDocument
import com.urlshortener.repository.StorageRepository
import com.urlshortener.repository.cache.CacheRepository
import com.urlshortener.service.hashGenerator.HashGenerator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.time.Instant

class UrlShortenerImplTest {
    private lateinit var urlShortener: UrlShortenerImpl
    private val mockStorage: StorageRepository = mock()
    private val mockCache: CacheRepository<String, String> = mock()
    private val mockGenerator: HashGenerator = mock()

    @BeforeEach
    fun setUp() {
        urlShortener = UrlShortenerImpl(mockGenerator, mockCache, mockStorage)
    }

    @Test
    fun `shorten should return a short URL starting with domain`() {
        val originalUrl = "https://example.com"
        whenever(mockCache.getByValue(originalUrl)).thenReturn(null)
        whenever(mockGenerator.encode(originalUrl)).thenReturn("abc123")
        whenever(mockStorage.save(any<UrlDocument>())).thenAnswer { it.getArgument<UrlDocument>(0) }

        val shortUrl = urlShortener.shorten(originalUrl)

        assertNotNull(shortUrl)
        assertTrue(shortUrl.startsWith(DOMAIN))
        assertEquals(DOMAIN + "abc123", shortUrl)
        verify(mockCache).save("abc123", originalUrl, TTL)
    }

    @Test
    fun `shorten should return cached short URL if exists`() {
        val originalUrl = "https://example.com"
        whenever(mockCache.getByValue(originalUrl)).thenReturn("abc123")

        val shortUrl = urlShortener.shorten(originalUrl)

        assertEquals(DOMAIN + "abc123", shortUrl)
        verify(mockGenerator, never()).encode(any())
        verify(mockStorage, never()).save(any())
    }

    @Test
    fun `retrieve should return original URL from cache`() {
        val shortUrl = DOMAIN + "abc123"
        whenever(mockCache.get("abc123")).thenReturn("https://example.com")

        val originalUrl = urlShortener.retrieve(shortUrl)

        assertEquals("https://example.com", originalUrl)
        verify(mockStorage, never()).findUrlDocumentByShortUrl(any())
    }

    @Test
    fun `retrieve should fetch from storage if cache misses`() {
        val shortUrl = DOMAIN + "abc123"
        whenever(mockCache.get("abc123")).thenReturn(null)
        val doc = UrlDocument(shortUrl = "abc123", longUrl = "https://example.com", createdAt = Instant.now())
        whenever(mockStorage.findUrlDocumentByShortUrl("abc123")).thenReturn(doc)

        doNothing().`when`(mockCache).save("abc123", doc.longUrl, TTL)

        val originalUrl = urlShortener.retrieve(shortUrl)

        assertEquals(doc.longUrl, originalUrl)
        verify(mockCache).save("abc123", doc.longUrl, TTL)
    }

    @Test
    fun `retrieve should return null if not found`() {
        val shortUrl = DOMAIN + "abc123"
        whenever(mockCache.get("abc123")).thenReturn(null)
        whenever(mockStorage.findUrlDocumentByShortUrl("abc123")).thenReturn(null)

        val originalUrl = urlShortener.retrieve(shortUrl)

        assertNull(originalUrl)
    }
}