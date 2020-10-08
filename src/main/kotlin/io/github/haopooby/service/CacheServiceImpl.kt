package io.github.haopooby.service

import io.github.haopooby.entity.Ads
import io.github.haopooby.model.Counter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class CacheServiceImpl : CacheService {

    @Autowired
    private lateinit var manager: CacheManager

    override fun ads() = manager.getCache("ads")!!
    override fun ads(index: Int) = ads().get(index)?.get() as Ads?
            ?: error("Index $index of Ads not found")

    override fun counters() = manager.getCache("counters")!!
    override fun counters(key: String, action: () -> Counter) = counters().get(key, action)!!

    override fun counters(userId: String, ads: Ads) =
            counters("$userId:${ads.id}") {
                val counter = Counter(ads)
                countersByUserIdRc1(userId)[ads.id] = counter
                counter
            }

    override fun countersByUserId() = manager.getCache("userCounters")!!
    fun countersByUserIdRc1(userId: String) = countersByUserId().get(userId) {
        ConcurrentHashMap<String, Counter>()
    }!!

    override fun forUser(userId: String) = UserImpl(countersByUserIdRc1(userId))

    class UserImpl(private val counters: Map<String, Counter>,
                   private val blockedList: Set<Counter> = blockedList(counters)
    ) : CacheService.ForUser {

        companion object {
            fun blockedList(map: Map<String, Counter>) = map
                    .mapNotNull { it.value }
                    .filterNot { it.allowed() }
                    .toSet()
        }

        override fun blockedList() = blockedList
    }
}