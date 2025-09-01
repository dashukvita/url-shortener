package com.urlshortener.service.hashGenerator

import com.urlshortener.constants.Constants.HASH_ALGORITHM
import com.urlshortener.constants.Constants.SHORT_BYTES_LENGTH
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

@Service
class HashGeneratorImpl : HashGenerator {

    override fun encode(input: String): String {
        try {
            val md = MessageDigest.getInstance(HASH_ALGORITHM)
            val digest = md.digest(input.toByteArray(StandardCharsets.UTF_8))
            val shortBytes = digest.copyOf(SHORT_BYTES_LENGTH)

            return Base62.encode(shortBytes)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Hash algorithm not found: $HASH_ALGORITHM", e)
        }
    }
}