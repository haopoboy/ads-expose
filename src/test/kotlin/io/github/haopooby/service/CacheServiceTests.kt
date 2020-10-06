package io.github.haopooby.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CacheServiceTests {

    @Autowired
    private lateinit var impl: CacheService

    @Test
    fun ads() {
        assertThat(impl.ads()).isNotNull
    }

    @Test
    fun adsByIndex() {
        assertThat(impl.ads(0)).isNotNull
    }

    @Test
    fun counters() {
        assertThat(impl.counters()).isNotNull
    }

    @Test
    fun countersByKey() {
        assertThat(impl.counters("id")).isNotNull
    }

    @Test
    fun forUser() {
        assertThat(impl.forUser("userId")).isNotNull
    }
}