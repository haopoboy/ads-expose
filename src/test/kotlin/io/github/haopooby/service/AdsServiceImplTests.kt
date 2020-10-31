package io.github.haopooby.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AdsServiceImplTests {

    @Autowired
    private lateinit var impl: AdsServiceImpl

    @Test
    fun exposeValid() {
        impl.exposeValid("userId").apply {
            assertThat(this).isNotNull
        }
    }
}