package com.urlshortener.service

import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.service.hashGenerator.HashGenerator
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class UrlShortenerImpl(
    val generator: HashGenerator
) : UrlShortener {

    private val shortToLong: MutableMap<String, String> = ConcurrentHashMap()
    private val longToShort: MutableMap<String, String> = ConcurrentHashMap()

    override fun shorten(originalUrl: String): String {
        longToShort[originalUrl]?.let { return DOMAIN + it }

        val shortCode = generator.encode(originalUrl)

        shortToLong[shortCode] = originalUrl
        longToShort[originalUrl] = shortCode

        return DOMAIN + shortCode
    }

    override fun retrieve(shortUrl: String): String? {
        if (!shortUrl.startsWith(DOMAIN)) return null
        val key = shortUrl.substring(DOMAIN.length)
        return shortToLong[key]
    }
}