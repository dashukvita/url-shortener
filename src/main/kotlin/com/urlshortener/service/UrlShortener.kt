package com.urlshortener.service

interface UrlShortener {
    fun shorten(originalUrl: String): String
    fun retrieve(shortUrl: String): String?
}