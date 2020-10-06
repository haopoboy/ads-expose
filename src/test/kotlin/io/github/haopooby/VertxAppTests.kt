package io.github.haopooby

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = ["app.vertx.enable=true"])
class VertxAppTests {

    @Test
    fun empty() {
    }

}
