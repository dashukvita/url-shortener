package com.urlshortener.constants

import java.time.Duration

object Constants {
    const val DOMAIN = "https://short.ly/"

    const val SHORT_BYTES_LENGTH = 6
    const val HASH_ALGORITHM = "SHA-256"
    const val MAX_ATTEMPTS = 10

    const val SHORT_KEY_PREFIX = "short:"
    const val LONG_KEY_PREFIX = "long:"
    val TTL: Duration = Duration.ofDays(30)
}