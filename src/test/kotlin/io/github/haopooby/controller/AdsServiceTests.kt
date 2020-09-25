package io.github.haopooby.controller

import io.github.haopooby.service.AdsService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AdsServiceTests {

    @Autowired
    private lateinit var impl: AdsService

    @Test
    fun exposeTo() {
        impl.exposeTo((1..100).random().toString())
    }
}