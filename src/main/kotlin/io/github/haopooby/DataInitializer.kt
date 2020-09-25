package io.github.haopooby

import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.AdsRepository
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

    override fun onApplicationEvent(p0: ApplicationReadyEvent) {
        initAds()
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
}