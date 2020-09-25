package io.github.haopooby.service

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
    override fun ads(index: Int) = ads().get(index)!!.get() as Ads?
            ?: error("Index $index of Ads not found")

    override fun counters() = manager.getCache("counters")!!
    override fun counters(key: String): Counter = counters().get(key) { Counter() }!!
}