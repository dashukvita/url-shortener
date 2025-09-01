package com.urlshortener.service.hashGenerator

import java.math.BigInteger

object Base62 {
    private const val ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private const val BASE = ALPHABET.length

    fun encode(bytes: ByteArray): String {
        var num = BigInteger(1, bytes)
        if (num == BigInteger.ZERO) return "0"

        val sb = StringBuilder()
        while (num > BigInteger.ZERO) {
            val (quotient, remainder) = num.divideAndRemainder(BigInteger.valueOf(BASE.toLong()))
            sb.append(ALPHABET[remainder.toInt()])
            num = quotient
        }
        return sb.reverse().toString()
    }
}