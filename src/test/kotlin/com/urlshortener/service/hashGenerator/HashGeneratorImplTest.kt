package com.urlshortener.service.hashGenerator

import org.junit.jupiter.api.Assertions.*

import com.urlshortener.exception.UniqueHashGenerationException
import com.urlshortener.repository.StorageRepository
import com.urlshortener.repository.cache.CacheRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class HashGeneratorImplTest {

    private lateinit var cache: CacheRepository<String, String>
    private lateinit var storage: StorageRepository
    private lateinit var hashGenerator: HashGeneratorImpl

    @BeforeEach
    fun setup() {
        cache = mock()
        storage = mock()
        hashGenerator = HashGeneratorImpl(cache, storage)
    }

    @Test
    fun `encode should return a hash for new URL`() {
        val url = "https://example.com"

        whenever(cache.contains(anyString())).thenReturn(false)
        whenever(storage.findUrlDocumentByShortUrl(anyString())).thenReturn(null)

        val hash = hashGenerator.encode(url)

        assertNotNull(hash)
        assertTrue(hash.isNotBlank())
        assertEquals(hash.length, hashGenerator.encode(url).length)
    }

    @Test
    fun `encode should retry on hash collision`() {
        val url = "https://example.com"

        var attempt = 0
        whenever(cache.contains(anyString())).thenAnswer {
            attempt++ < 1
        }
        whenever(storage.findUrlDocumentByShortUrl(anyString())).thenReturn(null)

        val hash = hashGenerator.encode(url)

        assertNotNull(hash)
        assertTrue(hash.isNotBlank())
    }

    @Test
    fun `encode should throw UniqueHashGenerationException after max attempts`() {
        val url = "https://example.com"

        whenever(cache.contains(anyString())).thenReturn(true)
        whenever(storage.findUrlDocumentByShortUrl(anyString())).thenReturn(null)

        val exception = assertThrows(UniqueHashGenerationException::class.java) {
            hashGenerator.encode(url)
        }

        assertTrue(exception.message!!.contains("Failed to generate unique short URL"))
    }

    @Test
    fun `encode should throw RuntimeException for invalid algorithm`() {
        val invalidHashGenerator = object : HashGeneratorImpl(cache, storage) {
            override fun encode(input: String): String {
                val field = HashGeneratorImpl::class.java.getDeclaredField("RANDOM")
                field.isAccessible = true
                return try {
                    val md = MessageDigest.getInstance("NON_EXISTENT_ALGO")
                    ""
                } catch (e: NoSuchAlgorithmException) {
                    throw RuntimeException("Hash algorithm not found: NON_EXISTENT_ALGO", e)
                }
            }
        }

        val exception = assertThrows(RuntimeException::class.java) {
            invalidHashGenerator.encode("https://example.com")
        }

        assertTrue(exception.message!!.contains("Hash algorithm not found"))
    }
}