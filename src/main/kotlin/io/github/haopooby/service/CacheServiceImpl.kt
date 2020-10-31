package io.github.haopooby.service

import com.github.benmanes.caffeine.cache.Cache
import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.Exposed
import io.github.haopooby.model.Counter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import javax.annotation.PostConstruct

@Service
@ConditionalOnProperty(prefix = "spring", name = ["cache.type"], havingValue = "CAFFEINE")
class CacheServiceImpl : CacheService {

    companion object {
        fun blockedList(map: Map<String, Counter>) = map
                .mapNotNull { it.value }
                .filterNot { it.allowed() }
                .map { it.ads }
                .toSet()
    }

    @Autowired
    private lateinit var manager: CacheManager
    private lateinit var ads: CacheService.ForAds
    private lateinit var exposed: CacheService.ForExposed
    private lateinit var counter: CountersImpl

    @PostConstruct
    private fun postConstruct() {
        ads = AdsImpl()
        exposed = ExposedImpl()
        counter = CountersImpl()
    }

    override fun ads() = ads
    override fun exposed() = exposed
    override fun counter(userId: String, ads: Ads) = UserCounterImpl(userId, ads)
    override fun forUser(userId: String) = UserImpl(countersByUserId(userId))

    fun countersByUserId() = manager.getCache("userCounter")!!
    fun countersByUserId(userId: String) = countersByUserId().get(userId) {
        ConcurrentHashMap<String, Counter>()
    }!!

    inner class AdsImpl : DefaultPathImpl<Int, Ads>("ads"), CacheService.ForAds {
        /**
         * Improve performance
         */
        override fun random() = get((0 until count()).random().toInt())
    }

    inner class ExposedImpl : DefaultPathImpl<String, Exposed>("exposed"), CacheService.ForExposed {
        override fun clear() {
            super<DefaultPathImpl>.clear()
            counter.clear()
            countersByUserId().clear()
        }
    }

    inner class CountersImpl : DefaultPathImpl<String, Counter>("counter") {
        fun get(userId: String, ads: Ads) = get("$userId:${ads.id}") {
            val counter = Counter(ads)
            countersByUserId(userId)[ads.id] = counter
            counter
        }
    }

    inner class UserCounterImpl(private val userId: String,
                                private val ads: Ads,
                                private val counter: Counter = this@CacheServiceImpl.counter.get(userId, ads))
        : CacheService.ForUserCounter {
        override fun allowed() = counter.allowed()
        override fun put(exposed: Exposed) = counter.increase()
    }

    open inner class DefaultPathImpl<K, T>(name: String) : PathImpl<K, T>(manager, name) {
        override fun count() = castAs(Cache::class.java).estimatedSize()

        @Suppress("UNCHECKED_CAST")
        override fun random() = castAs(Cache::class.java)
                .asMap().map { it.value }.random() as T
    }

    abstract class PathImpl<K, T>(private val manager: CacheManager, private val name: String) : CacheService.Path<K, T> {
        override fun get() = manager.getCache(name)!!

        @Suppress("UNCHECKED_CAST")
        override fun get(key: K) = get().get(key as Any)?.get() as T?
                ?: error("Index $key of $name not found")
    }

    /**
     * Good performance from a big number of users and Ads
     */
    inner class UserWithReMappingImpl(
            private val counters: Map<String, Counter>,
            private val blockedList: Set<Ads> = blockedList(counters)
    ) : CacheService.ForUser {
        override fun blockedList() = blockedList
        override fun random() = randomWithReMapping()

        private val blockedToAllowed: Map<Ads, Ads> = mapBlockedToAllowed(blockedList)

        private fun randomWithReMapping(): Ads {
            val ads = if (ads.count() > 0) {
                ads().random()
            } else {
                Ads.NO_ADS
            }
            return blockedToAllowed.getOrDefault(ads, ads)
        }

        private fun mapBlockedToAllowed(blockedList: Set<Ads>): Map<Ads, Ads> {
            val allAds = ads().castAs(Cache::class.java)
                    .asMap()
                    .mapNotNull { it.value as Ads }

            return blockedList.map {
                var ads: Ads? = null
                val iterator = allAds.iterator()
                while (blockedList.contains(ads) && iterator.hasNext()) {
                    ads = iterator.next()
                }
                it to (ads ?: Ads.NO_ADS)
            }.toMap()
        }
    }

    inner class UserImpl(
            private val counters: Map<String, Counter>,
            private val blockedList: Set<Ads> = blockedList(counters)
    ) : CacheService.ForUser {

        override fun blockedList() = blockedList
        override fun random() = randomWithRatio()

        private fun randomWithRatio(): Ads {
            val adsCount = ads().count()
            val blockedRatio = blockedList.size.toDouble() / adsCount
            // TODO: Find the best ratio for performance
            val preferredRandom = blockedRatio <= 0.5
            return if (preferredRandom) {
                ads().random()
            } else {
                randomWithSet()
            }
        }

        /**
         * First method with blocked list, bad performance on a big number of Ads
         */
        @Suppress("UNCHECKED_CAST")
        fun randomWithSet(): Ads {
            val ads = ads().castAs(Cache::class.java).asMap() as ConcurrentMap<String, Ads>
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

}