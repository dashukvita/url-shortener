package com.urlshortener.service

import com.urlshortener.constants.Constants.CHAR_POOL
import com.urlshortener.constants.Constants.DOMAIN
import com.urlshortener.constants.Constants.SHORT_LENGTH
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.util.concurrent.ConcurrentHashMap

@Service
class UrlShortenerImpl : UrlShortener {

    private val shortToLong: MutableMap<String, String> = ConcurrentHashMap()
    private val longToShort: MutableMap<String, String> = ConcurrentHashMap()

    private val random = SecureRandom()
    override fun shorten(originalUrl: String): String {
        longToShort[originalUrl]?.let { return DOMAIN + it }

        val shortCode = generateUniqueCode()

        shortToLong[shortCode] = originalUrl
        longToShort[originalUrl] = shortCode

        return DOMAIN + shortCode
    }

    override fun retrieve(shortUrl: String): String? {
        if (!shortUrl.startsWith(DOMAIN)) return null
        val key = shortUrl.substring(DOMAIN.length)
        return shortToLong[key]
    }

    private fun generateUniqueCode(): String {
        var shortCode: String
        do {
            shortCode = (1..SHORT_LENGTH)
                .map { CHAR_POOL[random.nextInt(CHAR_POOL.size)] }
                .joinToString("")
        } while (shortToLong.containsKey(shortCode))
        return shortCode
    }
}