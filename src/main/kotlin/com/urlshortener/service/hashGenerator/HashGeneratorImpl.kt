package com.urlshortener.service.hashGenerator

import com.urlshortener.constants.Constants.HASH_ALGORITHM
import com.urlshortener.constants.Constants.MAX_ATTEMPTS
import com.urlshortener.constants.Constants.SHORT_BYTES_LENGTH
import com.urlshortener.exception.UniqueHashGenerationException
import com.urlshortener.repository.StorageRepository
import com.urlshortener.repository.cache.CacheRepository
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

/**
 * Generates short hash codes for URLs using SHA-256 + Base62 and ensures uniqueness.
 * Retries with random byte changes up to MAX_ATTEMPTS if collisions occur.
 */

@Service
class HashGeneratorImpl(
    private val cache: CacheRepository<String, String>,
    private val storage: StorageRepository
) : HashGenerator {

    companion object {
        private val RANDOM = SecureRandom()
    }

    override fun encode(input: String): String {
        try {
            val md = MessageDigest.getInstance(HASH_ALGORITHM)
            val digest = md.digest(input.toByteArray(StandardCharsets.UTF_8))

            val shortBytes = digest.copyOf(SHORT_BYTES_LENGTH)
            var hash = Base62.encode(shortBytes)
            var attempt = 0

            while (isHashExist(hash) && attempt < MAX_ATTEMPTS) {
                val randomIndex = RANDOM.nextInt(shortBytes.size)
                shortBytes[randomIndex] = RANDOM.nextInt(256).toByte()
                hash = Base62.encode(shortBytes)
                attempt++
            }

            if (attempt >= MAX_ATTEMPTS) {
                throw UniqueHashGenerationException(
                    "Failed to generate unique short URL after $MAX_ATTEMPTS attempts"
                )
            }

            return hash
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Hash algorithm not found: $HASH_ALGORITHM", e)
        }
    }

    /** Check if hash already exists */
    private fun isHashExist(hash: String): Boolean =
        cache.contains(hash) || storage.findUrlDocumentByShortUrl(hash) != null
}