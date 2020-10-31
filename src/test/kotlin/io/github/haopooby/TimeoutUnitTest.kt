package io.github.haopooby

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.assertTimeoutPreemptively
import java.time.Duration
import java.util.concurrent.TimeoutException

/**
 * Junit5 @Timeout is not working in current version, use assertTimeoutPreemptively() instead and
 * see [issue](https://github.com/junit-team/junit5/issues/2087) for more details.
 */
class TimeoutUnitTest {

    @Test
    @Timeout(5)
    @Disabled
    fun timeout() {
        assertThatThrownBy {
            assertTimeoutPreemptively(Duration.ofSeconds(5)) {
                while (true) {
                }
            }
        }.isInstanceOf(InterruptedException::class.java)
    }
}
