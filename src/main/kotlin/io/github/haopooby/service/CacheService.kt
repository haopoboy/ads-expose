package io.github.haopooby.service

import io.github.haopooby.entity.Ads
import io.github.haopooby.model.Counter
import org.springframework.cache.Cache

interface CacheService {
    fun ads(): Cache
    @Suppress("UNCHECKED_CAST")
    fun <T> adsAs(type: Class<T>) = ads().nativeCache as T
    fun ads(index: Int): Ads
    fun counters(): Cache
    fun counters(key: String, action: () -> Counter): Counter
    fun counters(key: String): Counter?

    @Suppress(names = ["UNCHECKED_CAST"])
    fun forUser(userId: String): ForUser

    interface ForUser {
        fun blockedList(): List<Counter>
    }
}
