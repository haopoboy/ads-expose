package io.github.haopooby.service

import io.github.haopooby.entity.Ads
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
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

    @SpringBootTest(properties = ["app.ads.count=0"])
    @Nested
    @DisplayName("when zero ads")
    inner class ZeroAds {

        @Autowired
        private lateinit var impl: AdsServiceImpl

        @Test
        fun `random() should be no ads`() {
            assertThat(impl.random().title).isEqualTo(Ads.NO_ADS.title)
        }

        @Test
        fun `exposeValid() should be no ads`() {
            assertThat(impl.exposeValid("userId").title).isEqualTo(Ads.NO_ADS.title)
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
            impl.cacheService.counters().clear()
            impl.cacheService.countersByUserId().clear()
        }

        @Test
        fun random() {
            assertThat(impl.random()).isNotNull
        }

        @Test
        fun `random() 10 times`() {
            val grouped = (1..10).map { _ ->
                impl.random()
            }.groupBy { it }

            assertThat(grouped).hasSize(1)
            assertThat(grouped.values.first()).hasSize(10)
        }

        @Test
        fun `random() 10 times from same user`() {
            val grouped = (1..10).map { _ ->
                impl.random("userId")
            }.groupBy { it }

            assertThat(grouped).hasSize(1)
            assertThat(grouped.values.first()).hasSize(10)
        }

        @Test
        fun `exposeValid() 10 times from same user`() = shouldNotBeInfinity {
            val grouped = (1..10).map { _ ->
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
            impl.cacheService.counters().clear()
            impl.cacheService.countersByUserId().clear()
        }

        @Test
        fun random() {
            assertThat(impl.random()).isNotNull
        }

        @Test
        fun `exposeValid() 30 times from same user`() = shouldNotBeInfinity {
            val grouped = (1..30).map { _ ->
                impl.exposeValid("userId")
            }.groupBy { it }

            assertThat(grouped).hasSize(10)
        }

        @Test
        fun `exposeValid() 35 times from same user`() = shouldNotBeInfinity {
            val grouped = (1..35).map { _ ->
                impl.exposeValid("userId")
            }.groupBy { it }

            assertThat(grouped).hasSize(11)
            assertThat(grouped.values.last().first()).isEqualTo(Ads.NO_ADS)
        }
    }
}