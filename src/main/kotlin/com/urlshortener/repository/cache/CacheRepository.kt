package com.urlshortener.repository.cache

import java.time.Duration

interface CacheRepository<K, V> {
    fun save(key: K, value: V, ttl: Duration)
    fun get(key: K): V?
    fun getByValue(value: V): K?
    fun contains(key: K): Boolean
}