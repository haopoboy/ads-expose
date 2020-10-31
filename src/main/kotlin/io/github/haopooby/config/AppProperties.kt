package io.github.haopooby.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app")
data class AppProperties(
        var vertx: Vertx = Vertx(),
        var ads: Ads = Ads()
) {

    data class Vertx(
            var enable: Boolean = true
    )

    data class Ads(
            var count: Int = 10_000
    )
}