package com.urlshortener.service

import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.model.UrlDocument
import com.urlshortener.repository.StorageRepository
import com.urlshortener.service.hashGenerator.HashGeneratorImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.time.Instant

class UrlShortenerImplTest {
    private lateinit var urlShortener: UrlShortenerImpl
    private val mockStorage: StorageRepository = mock()
    private val generator = HashGeneratorImpl()

    @BeforeEach
    fun setUp() {
        urlShortener = UrlShortenerImpl(generator, mockStorage)
    }

    @Test
    fun `shorten should return a short URL starting with domain`() {
        val originalUrl = "https://example.com"
        whenever(mockStorage.findUrlDocumentByLongUrl(originalUrl)).thenReturn(null)
        whenever(mockStorage.save(UrlDocument(shortUrl = "abc123", longUrl = originalUrl, createdAt = Instant.now())))
            .thenAnswer { }

        val shortUrl = urlShortener.shorten(originalUrl)
        assertNotNull(shortUrl)
        assertTrue(shortUrl.startsWith(DOMAIN))
    }

    @Test
    fun `shorten should return the same short URL for the same original URL`() {
        val originalUrl = "https://example.com"
        val doc = UrlDocument(shortUrl = "abc123", longUrl = originalUrl, createdAt = Instant.now())
        whenever(mockStorage.findUrlDocumentByLongUrl(originalUrl)).thenReturn(doc)

        val shortUrl1 = urlShortener.shorten(originalUrl)
        val shortUrl2 = urlShortener.shorten(originalUrl)

        assertEquals(shortUrl1, shortUrl2)
    }

    @Test
    fun `retrieve should return original URL for given shortURL`() {
        val shortCode = "abc123"
        val shortUrl = DOMAIN + shortCode
        val originalUrl = "https://example.com"
        val doc = UrlDocument(shortUrl = shortCode, longUrl = originalUrl, createdAt = Instant.now())
        whenever(mockStorage.findUrlDocumentByShortUrl(shortCode)).thenReturn(doc)

        val retrieved = urlShortener.retrieve(shortUrl)
        assertEquals(originalUrl, retrieved)
    }

    @Test
    fun `retrieve should return null for unknown URL`() {
        val unknownShortUrl = DOMAIN + "abcd"
        whenever(mockStorage.findUrlDocumentByShortUrl("abcd")).thenReturn(null)

        val retrieved = urlShortener.retrieve(unknownShortUrl)
        assertNull(retrieved)
    }

    @Test
    fun `shorten should generate unique codes for different URLs`() {
        val url1 = "https://example.com/1"
        val url2 = "https://example.com/2"
        whenever(mockStorage.findUrlDocumentByLongUrl(url1)).thenReturn(null)
        whenever(mockStorage.findUrlDocumentByLongUrl(url2)).thenReturn(null)
        whenever(mockStorage.save(any<UrlDocument>())).thenAnswer { invocation ->
            invocation.getArgument<UrlDocument>(0)
        }

        val short1 = urlShortener.shorten(url1)
        val short2 = urlShortener.shorten(url2)

        assertNotEquals(short1, short2)
    }
}