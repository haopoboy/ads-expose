package io.github.haopooby

import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.AdsRepository
import io.github.haopooby.service.CacheService
import me.tongfei.progressbar.ProgressBar
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import kotlin.streams.toList

@Component
class DataInitializer : ApplicationListener<ApplicationReadyEvent> {
    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }

    @Autowired
    private lateinit var adsRepository: AdsRepository

    @Autowired
    private lateinit var cacheService: CacheService

    override fun onApplicationEvent(p0: ApplicationReadyEvent) {
        initAds()
        initCaches()
    }

    fun initAds() {
        if (adsRepository.count() > 0) {
            logger.info("Found Ads, skip initialization")
        }
        val size = 1_000
        val page = 10
        ProgressBar("Persisting Ads", (size * page).toLong()).use { progress ->
            (1..page).toList().parallelStream().forEach { _ ->
                (1..size).toList()
                        .parallelStream()
                        .map { Ads("Ads $it") }.toList()
                        .let { adsRepository.saveAll(it) }
                progress.stepBy(size.toLong())
            }
        }

        logger.info("${Utils.formatAsDecimal(adsRepository.count())} Ads persisted")
    }

    fun initCaches() {
        val ads = cacheService.ads()
        val count = adsRepository.count()
        ProgressBar("Caching Ads", count).use { progress ->
            adsRepository.findAll().forEachIndexed { index, it ->
                ads.put(index, it)
                progress.step()
            }
        }
        logger.info("${Utils.formatAsDecimal(count)} Ads cached")
    }
}