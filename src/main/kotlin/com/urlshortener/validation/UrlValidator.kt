package com.urlshortener.validation

import java.net.*

object UrlValidator {

    fun isNotValidUrl(url: String): Boolean {
        if (url.isBlank()) return true

        return try {
            val uri = URI(url.trim())

            val scheme = uri.scheme
            if (!scheme.equals("http", ignoreCase = true) && !scheme.equals("https", ignoreCase = true)) return true

            val host = uri.host
            if (host.isNullOrBlank()) return true

            try {
                val address = InetAddress.getByName(host)
                address.isLoopbackAddress || address.isAnyLocalAddress || address.isSiteLocalAddress
            } catch (e: UnknownHostException) {
                true
            }
        } catch (e: URISyntaxException) {
            true
        }
    }
}