package com.urlshortener.repository.cache

import com.urlshortener.constants.Constants.LONG_KEY_PREFIX
import com.urlshortener.constants.Constants.SHORT_KEY_PREFIX
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>
) : CacheRepository<String, String> {

    override fun save(key: String, value: String, ttl: Duration) {
        redisTemplate.opsForValue().set(SHORT_KEY_PREFIX + key, value, ttl)
        redisTemplate.opsForValue().set(LONG_KEY_PREFIX + value, key, ttl)
    }

    override fun get(key: String): String? {
        return redisTemplate.opsForValue().get(SHORT_KEY_PREFIX + key)
    }

    override fun getByValue(value: String): String? {
        return redisTemplate.opsForValue().get(LONG_KEY_PREFIX + value)
    }

    override fun contains(key: String): Boolean {
        return redisTemplate.hasKey(SHORT_KEY_PREFIX + key) ?: false
    }
}