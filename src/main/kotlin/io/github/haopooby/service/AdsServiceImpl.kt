package io.github.haopooby.service

import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.Exposed
import io.github.haopooby.entity.ExposedRepository
import io.github.haopooby.model.Counter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdsServiceImpl : AdsService {

    @Autowired
    private lateinit var exposedRepository: ExposedRepository

    @Autowired
    private lateinit var cacheService: CacheService

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
            ads = random()
            counter = cacheService.counters("$userId${ads.id}")
        } while (!counter.allowed())
        counter.increase()
        return ads
    }

    override fun random() = cacheService.ads((0..9_999).random())
}