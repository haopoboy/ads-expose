package io.github.haopooby.service

import io.github.haopooby.entity.ExposedRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AdsServiceTests {

    @Autowired
    private lateinit var impl: AdsService

    @Autowired
    private lateinit var exposedRepository: ExposedRepository

    @Test
    fun exposeFor() {
        val ads = impl.exposeFor((1..100).random().toString())
        assertThat(ads).isNotNull

        // Wait for coroutines
        Thread.sleep(2000)
        assertThat(exposedRepository.count()).isNotZero()
    }

    @Test
    fun random() {
        assertThat(impl.random()).isNotNull
    }
}