package io.github.haopooby.service

import io.github.haopooby.Profiles
import io.github.haopooby.config.AppProperties
import io.github.haopooby.entity.Ads
import io.github.haopooby.entity.Exposed
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.test.context.ActiveProfiles

class CacheServiceTests {
    @Nested
    inner class StackImpl : Stack()

    @Nested
    inner class AdsImpl(@Autowired impl: CacheService) : PathStack<Int, Ads>(impl.ads()) {
        @Autowired
        private lateinit var properties: AppProperties

        override fun key() = 0
        override fun value() = Ads()

        @Test
        override fun count() {
            assertThat(impl.count()).isEqualTo(properties.ads.count.toLong())
            super.count()
        }
    }

    @Nested
    @ActiveProfiles(Profiles.REDIS)
    inner class AdsRedisImpl(@Autowired impl: CacheService) : AdsImpl(impl) {

        @Autowired
        private lateinit var redisTemplate: RedisTemplate<String, Any>

        @Test
        fun ops() {
            val count = redisTemplate.execute { connection ->
                val options = ScanOptions
                        .scanOptions()
                        .match("ads::*")
                        .build()
                connection.scan(options).use {
                    it.asSequence().toList().size
                }
            }!!
        }

    }

    @Nested
    inner class ExposedImpl(@Autowired impl: CacheService) : PathStack<String, Exposed>(impl.exposed()) {

        override fun key() = "random"
        override fun value() = Exposed()

        @AfterEach
        fun clear() = impl.clear()

        @Test
        override fun getByKey() {
            impl.put(key(), value())
            super.getByKey()
        }
    }

    @SpringBootTest(properties = ["app.ads.count=100"])
    abstract class PathStack<K, T>(protected val impl: CacheService.Path<K, T>) {

        @Test
        fun get() {
            assertThat(impl.get()).isNotNull
        }

        @Test
        fun count() {
            assertThat((impl.count())).isNotNull()
        }

        @Test
        fun getByKey() {
            assertThat(impl.get(key())).isNotNull
        }

        @Test
        fun getOr() {
            assertThat(impl.get(key()) { value() }).isNotNull
        }

        abstract fun key(): K
        abstract fun value(): T
    }

    @SpringBootTest
    abstract class Stack {
        @Autowired
        private lateinit var impl: CacheService

        @Test
        fun forUser() {
            assertThat(impl.forUser("userId")).isNotNull
        }
    }
}