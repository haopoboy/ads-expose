package io.github.haopooby.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("vertx")
data class VertxProperties(
        var enable: Boolean = true
)