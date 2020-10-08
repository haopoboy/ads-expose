package io.github.haopooby.service

import com.github.benmanes.caffeine.cache.Cache
import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.Exposed
import io.github.haopooby.entity.ExposedRepository
import io.github.haopooby.model.Counter
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

    override fun exposeFor(userId: String): Ads {
        val ads = exposeValid(userId)
        // Able to restore from scheduler
        GlobalScope.launch {
            exposedRepository.save(Exposed(userId, ads.id))
        }
        return ads
    }

    /**
     * Random expose Ads until it's allowed.
     */
    fun exposeValid(userId: String): Ads {
        var ads: Ads
        var counter: Counter
        do {
            ads = random(userId)
            counter = cacheService.counters(userId, ads)
        } while (!counter.allowed())

        counter.increase()
        return ads
    }

    override fun random(userId: String): Ads {
        val adsCount = count()
        val blockedList = cacheService.forUser(userId)
                .blockedList()
                .map { it.ads }
                .toSet()

        val blockedRatio = blockedList.size.toDouble() / adsCount
        val preferredRandom = blockedRatio <= 0.5
        return if (preferredRandom) {
            cacheService.ads((0 until adsCount).random())
        } else {
            randomWith(blockedList)
        }
    }

    /**
     * @blockedList uses to filter the list
     */
    @Suppress("UNCHECKED_CAST")
    fun randomWith(blockedList: Set<Ads> = setOf()): Ads {
        val ads = cacheService.adsAs(Cache::class.java).asMap() as ConcurrentMap<String, Ads>
        val filtered = ads
                .mapNotNull { it.value }
                .toSet() - blockedList

        return if (filtered.isEmpty()) {
            Ads.NO_ADS
        } else {
            filtered.random()
        }
    }

    fun count(): Int {
        val cache = cacheService.adsAs(Cache::class.java)
        return cache.estimatedSize().toInt()
    }
}