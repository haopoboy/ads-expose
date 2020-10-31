package io.github.haopooby.service

import com.github.benmanes.caffeine.cache.Cache
import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.Exposed
import io.github.haopooby.entity.ExposedRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentMap

@Service
class AdsServiceImpl : AdsService {

    @Autowired
    private lateinit var exposedRepository: ExposedRepository

    @Autowired
    internal lateinit var cacheService: CacheService

    override fun exposeFor(userId: String) = exposeValid(userId)

    /**
     * Random expose Ads until it's allowed.
     */
    fun exposeValid(userId: String): Ads {
        var ads: Ads
        var counter: CacheService.ForUserCounter
        do {
            ads = random(userId)
            counter = cacheService.counter(userId, ads)
        } while (!counter.allowed())

        Exposed(userId, ads.id).let {
            counter.put(it)
            GlobalScope.launch {
                exposedRepository.save(it)
            }
        }
        return ads
    }

    override fun random(userId: String) = cacheService.forUser(userId).random()

    /**
     * @blockedList remove blocked from the list to get allowed list
     */
    @Suppress("UNCHECKED_CAST")
    fun randomWith(blockedList: Set<Ads> = setOf()): Ads {
        val ads = cacheService.ads().castAs(Cache::class.java).asMap() as ConcurrentMap<String, Ads>
        val allowed = ads
                .mapNotNull { it.value }
                .toSet() - blockedList

        return if (allowed.isEmpty()) {
            Ads.NO_ADS
        } else {
            allowed.random()
        }
    }
}