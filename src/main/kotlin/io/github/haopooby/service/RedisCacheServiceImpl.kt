package io.github.haopooby.service

import io.github.haopooby.Utils
import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.Exposed
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
@ConditionalOnProperty(prefix = "spring", name = ["cache.type"], havingValue = "REDIS")
class RedisCacheServiceImpl : CacheService {

    @Autowired
    private lateinit var manager: CacheManager

    @Autowired
    private lateinit var template: RedisTemplate<String, Any>
    private lateinit var ads: CacheService.ForAds
    private lateinit var exposed: CacheService.ForExposed

    @PostConstruct
    private fun postConstruct() {
        ads = AdsImpl()
        exposed = ExposedImpl()
    }

    override fun ads() = ads
    override fun exposed() = exposed
    override fun forUser(userId: String) = UserImpl()
    override fun counter(userId: String, ads: Ads) = UserCounterImpl(
            userId, ads, RedisPathImpl<String, Exposed>("exposed::$userId:${ads.id}", Exposed::class.java).count()
    )

    inner class AdsImpl : RedisPathImpl<Int, Ads>("ads", Ads::class.java), CacheService.ForAds
    inner class ExposedImpl : RedisPathImpl<String, Exposed>("exposed", Exposed::class.java), CacheService.ForExposed

    inner class UserCounterImpl(private val userId: String,
                                private val ads: Ads,
                                private val count: Long
    ) : CacheService.ForUserCounter {
        override fun allowed() = count < ads.capNum || !ads.exposedLimited
        override fun put(exposed: Exposed) = exposed().put("$userId:${ads.id}:${exposed.id}", exposed)
    }

    open inner class RedisPathImpl<K, T>(private val name: String, private val type: Class<T>) : CacheServiceImpl.PathImpl<K, T>(manager, name) {
        override fun count() = template.execute { connection ->
            val options = ScanOptions
                    .scanOptions()
                    .match("ads::*")
                    .build()
            connection.scan(options).use {
                it.asSequence().toSet().size
            }
        }!!.toLong()

        override fun get(key: K) = Utils.getObjectMapper()
                .convertValue<T>(get().get(key as Any), type)!!

        override fun random(): T = TODO("Not implemented yet")
    }

    class UserImpl(private val blockedList: Set<Ads> = setOf()
    ) : CacheService.ForUser {
        override fun blockedList() = blockedList
        override fun random(): Ads {
            TODO("Not yet implemented")
        }
    }
}