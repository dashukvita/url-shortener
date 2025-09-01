package com.urlshortener.service.hashGenerator

interface HashGenerator {
    fun encode(input: String): String
}