package io.github.haopooby.service

import com.github.benmanes.caffeine.cache.Cache
import io.github.haopooby.entity.Ads
import io.github.haopooby.model.Counter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

@Service
class CacheServiceImpl : CacheService {

    @Autowired
    private lateinit var manager: CacheManager

    override fun ads() = manager.getCache("ads")!!
    override fun ads(index: Int) = ads().get(index)?.get() as Ads?
            ?: error("Index $index of Ads not found")

    override fun counters() = manager.getCache("counters")!!
    override fun counters(key: String, action: () -> Counter): Counter = counters().get(key, action)!!
    override fun counters(key: String) = counters(key) { Counter() }

    @Suppress("UNCHECKED_CAST")
    override fun forUser(userId: String) = UserImpl(counters().nativeCache as Cache<String, Counter>, userId)

    class UserImpl(private val counters: Cache<String, Counter>,
                   private val userId: String,
                   private val blockedList: List<Counter> = blockedList(counters, userId)
    ) : CacheService.ForUser {

        companion object {
            fun blockedList(cache: Cache<String, Counter>, userId: String) = cache.asMap()
                    .filter { it.key.startsWith("$userId:") }
                    .filter { !it.value.allowed() }
                    .mapNotNull { it.value }.toList()
        }

        override fun blockedList() = blockedList
    }
}