package io.github.haopooby.service

import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.Exposed
import io.github.haopooby.model.Counter
import org.springframework.cache.Cache

interface CacheService {

    fun ads(): ForAds
    fun exposed(): ForExposed
    fun counter(userId: String, ads: Ads): ForUserCounter
    fun forUser(userId: String): ForUser

    interface ForAds : Path<Int, Ads>
    interface ForExposed : Path<String, Exposed>

    interface ForUserCounter {
        fun allowed(): Boolean
        fun put(exposed: Exposed)
    }

    interface Path<K, T> {
        fun get(): Cache
        fun get(key: K): T
        fun get(key: K, action: () -> T) = get().get(key as Any, action)!!
        fun count(): Long
        fun clear() = get().clear()
        fun random(): T

        @Suppress("UNCHECKED_CAST")
        fun <T> castAs(type: Class<T>) = get().nativeCache as T

        /**
         * Constraint the type of key and value
         */
        fun put(key: K, value: T) = get().put(key!!, value)
    }

    interface ForUser {
        fun blockedList(): Set<Ads>
        fun random(): Ads
    }
}
