package io.github.haopooby.service

import io.github.haopooby.Profiles
import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.AdsRepository
import io.github.haopooby.entity.Exposed
import io.github.haopooby.entity.ExposedRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@Profile(Profiles.JPA)
class AdsJpaServiceImpl : AdsService {

    @Autowired
    private lateinit var adsRepository: AdsRepository

    @Autowired
    private lateinit var exposedRepository: ExposedRepository

    @Transactional
    override fun exposeTo(userId: String): Ads {
        val pageable = PageRequest.of((1..100).random(), 1)
        return adsRepository.findAll(pageable).first().also {
            exposedRepository.save(Exposed(userId, it.id))
        }
    }
}