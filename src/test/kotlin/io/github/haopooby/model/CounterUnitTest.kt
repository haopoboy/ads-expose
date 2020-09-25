package io.github.haopooby.model

import io.github.haopooby.entity.Ads
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CounterUnitTest {

    @Test
    fun buildFrom() {
        assertThat(Counter.buildCache(Ads())).isNotNull
    }

    @Test
    fun allowedAndIncrease() {
        Counter().apply {
            assertThat(this.allowed()).isTrue()
            this.increase()
            assertThat(this.allowed()).isTrue()
            this.increase()
            assertThat(this.allowed()).isTrue()
            this.increase()
            assertThat(this.allowed()).isFalse()
        }
    }
}