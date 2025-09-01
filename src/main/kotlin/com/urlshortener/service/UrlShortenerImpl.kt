package com.urlshortener.service

import com.urlshortener.aspect.Loggable
import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.constants.Constants.TTL
import com.urlshortener.model.UrlDocument
import com.urlshortener.repository.StorageRepository
import com.urlshortener.repository.cache.CacheRepository
import com.urlshortener.service.hashGenerator.HashGenerator
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UrlShortenerImpl(
    val generator: HashGenerator,
    private val cacheRepository: CacheRepository<String, String>,
    val storageRepository: StorageRepository
) : UrlShortener {

    /**
     * Shortens a long URL.
     * @param originalUrl original URL
     * @return shortened URL with domain
     */
    @Loggable
    override fun shorten(originalUrl: String): String {
        // simple protection from double click
        cacheRepository.getByValue(originalUrl)?.let { cached ->
            return DOMAIN + cached
        }

        val shortCode = generator.encode(originalUrl)
        val doc = UrlDocument(
            shortUrl = shortCode,
            longUrl = originalUrl,
            createdAt = Instant.now()
        )
        storageRepository.save(doc)
        cacheRepository.save(shortCode, originalUrl, TTL)

        return DOMAIN + shortCode
    }

    /**
     * Retrieves the original URL from the shortened one.
     * @param shortUrl shortened URL
     * @return Optional with the original URL or empty
     */
    @Loggable
    override fun retrieve(shortUrl: String): String? {
        val shortCode = shortUrl.removePrefix(DOMAIN)

        var originalUrl = cacheRepository.get(shortCode)
        if (originalUrl == null) {
            storageRepository.findUrlDocumentByShortUrl(shortCode)?.let { doc ->
                originalUrl = doc.longUrl
                cacheRepository.save(shortCode, originalUrl!!, TTL)
            }
        }

        return originalUrl
    }
}