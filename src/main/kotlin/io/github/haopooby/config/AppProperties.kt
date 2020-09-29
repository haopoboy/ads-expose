package io.github.haopooby.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("app")
data class AppProperties(var vertx: Vertx = Vertx()) {

    data class Vertx(
            var enable: Boolean = true
    )
}