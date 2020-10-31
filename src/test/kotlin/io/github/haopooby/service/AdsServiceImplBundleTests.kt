package io.github.haopooby.service

import io.github.haopooby.Profiles
import io.github.haopooby.entity.Ads
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.EnabledIf
import java.time.Duration

class AdsServiceImplBundleTests {

    /**
     * Junit5 @Timeout is not working in current version
     * @see [io.github.haopooby.TimeoutUnitTest]
     */
    fun shouldNotBeInfinity(action: () -> Unit) =
            assertTimeoutPreemptively(Duration.ofSeconds(5), "should not be infinity") {
                action.invoke()
            }

    @Nested
    @DisplayName("when zero ads")
    inner class ZeroAds : AbstractZeroAds()

    @Nested
    @DisplayName("when zero ads with Redis")
    @EnabledIf(expression = "#{environment.acceptsProfiles('${Profiles.REDIS}')}", loadContext = true)
    @ActiveProfiles(Profiles.REDIS)
    inner class RedisZeroAds : AbstractZeroAds()

    @SpringBootTest(properties = ["app.ads.count=0"])
    abstract class AbstractZeroAds {

        @Autowired
        private lateinit var impl: AdsServiceImpl

        @Test
        fun `random() should be no ads`() {
            assertThat(impl.random()).isEqualTo(Ads.NO_ADS)
        }

        @Test
        fun `exposeValid() should be no ads`() {
            assertThat(impl.exposeValid("userId")).isEqualTo(Ads.NO_ADS)
        }
    }

    @SpringBootTest(properties = ["app.ads.count=1"])
    @Nested
    @DisplayName("when one ads")
    inner class OneAds {

        @Autowired
        private lateinit var impl: AdsServiceImpl

        @AfterEach
        fun clear() {
            impl.cacheService.exposed().clear()
        }

        @Test
        fun random() {
            assertThat(impl.random()).isNotNull
        }

        @Test
        fun `random() 10 times`() {
            val grouped = (1..10).map {
                impl.random()
            }.groupBy { it }

            assertThat(grouped).hasSize(1)
            assertThat(grouped.values.first()).hasSize(10)
        }

        @Test
        fun `random() 10 times from same user`() {
            val grouped = (1..10).map {
                impl.random("userId")
            }.groupBy { it }

            assertThat(grouped).hasSize(1)
            assertThat(grouped.values.first()).hasSize(10)
        }

        @Test
        fun `exposeValid() 10 times`() = shouldNotBeInfinity {
            val grouped = (1..10).map {
                impl.exposeValid(it.toString())
            }.groupBy { it }
            assertThat(grouped).doesNotContainKey(Ads.NO_ADS)
        }

        @Test
        fun `exposeValid() 10 times from same user`() = shouldNotBeInfinity {
            val grouped = (1..10).map {
                impl.exposeValid("userId")
            }.groupBy { it }
            assertThat(grouped).hasSize(2)
            assertThat(grouped.values.last().first()).isEqualTo(Ads.NO_ADS)
        }
    }

    @SpringBootTest(properties = ["app.ads.count=10"])
    @Nested
    @DisplayName("when 10 ads")
    inner class TenAds {

        @Autowired
        private lateinit var impl: AdsServiceImpl

        @AfterEach
        fun clear() {
            impl.cacheService.exposed().clear()
        }

        @Test
        fun random() {
            assertThat(impl.random()).isNotNull
        }

        @Test
        fun `exposeValid() 30 times`() = shouldNotBeInfinity {
            val grouped = (1..30).map {
                impl.exposeValid(it.toString())
            }.groupBy { it }

            assertThat(grouped).doesNotContainKey(Ads.NO_ADS)
        }

        @Test
        fun `exposeValid() 30 times from same user`() = shouldNotBeInfinity {
            val grouped = (1..30).map {
                impl.exposeValid("userId")
            }.groupBy { it }

            assertThat(grouped).hasSize(10)
        }

        @Test
        fun `exposeValid() 35 times`() = shouldNotBeInfinity {
            val grouped = (1..35).map {
                impl.exposeValid(it.toString())
            }.groupBy { it }

            assertThat(grouped).doesNotContainKey(Ads.NO_ADS)
        }

        @Test
        fun `exposeValid() 35 times from same user`() = shouldNotBeInfinity {
            val grouped = (1..35).map {
                impl.exposeValid("userId")
            }.groupBy { it }

            assertThat(grouped).hasSize(11)
            assertThat(grouped.values.last().first()).isEqualTo(Ads.NO_ADS)
        }

        @Test
        fun `exposeValid() 35 times from same user rc`() {
            val grouped = (1..35).map {
                impl.exposeValid("userId")
            }.groupBy { it }

            assertThat(grouped).hasSize(11)
            assertThat(grouped.values.last().first()).isEqualTo(Ads.NO_ADS)
        }
    }
}