package com.urlshortener.service

import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.model.UrlDocument
import com.urlshortener.repository.StorageRepository
import com.urlshortener.service.hashGenerator.HashGenerator
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UrlShortenerImpl(
    val generator: HashGenerator,
    val storageRepository: StorageRepository
) : UrlShortener {

    override fun shorten(originalUrl: String): String {
        val existingDoc = storageRepository.findUrlDocumentByLongUrl(originalUrl)
        if (existingDoc != null) {
            return DOMAIN + existingDoc.shortUrl
        }

        val shortCode = generator.encode(originalUrl)
        val doc = UrlDocument(
            shortUrl = shortCode,
            longUrl = originalUrl,
            createdAt = Instant.now()
        )
        storageRepository.save(doc)

        return DOMAIN + shortCode
    }

    override fun retrieve(shortUrl: String): String? {
        val shortCode = shortUrl.removePrefix(DOMAIN)
        val doc = storageRepository.findUrlDocumentByShortUrl(shortCode)
        return doc?.longUrl
    }
}