package io.github.haopooby.service

import io.github.haopooby.entity.Ads
import io.github.haopooby.model.Counter
import org.springframework.cache.Cache

interface CacheService {
    fun ads(): Cache
    fun ads(index: Int): Ads
    fun counters(): Cache
    fun counters(key: String): Counter
}