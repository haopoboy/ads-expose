package io.github.haopooby

import io.github.haopooby.config.VertxProperties
import io.github.haopooby.service.AdsService
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@Component
class VertxApp : CoroutineVerticle(), ApplicationListener<ApplicationReadyEvent> {

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var adsService: AdsService

    @Autowired
    private lateinit var properties: VertxProperties

    override suspend fun start() {

        val router = Router.router(vertx).apply {
            this.get("/empty").coroutineHandler { it.response().end("empty") }
            this.get("/exposeTo").coroutineHandler {
                it.response().end(Json.encode(
                        adsService.exposeFor((1..100).random().toString())
                ))
            }
        }

        val apiRouter = Router.router(vertx).apply {
            this.mountSubRouter("/api", router)
            this.mountSubRouter("/", router)
        }
        vertx.createHttpServer().requestHandler(apiRouter).listenAwait(8080)
    }

    /**
     * An extension method for simplifying coroutines usage with Vert.x Web routers
     */
    fun Route.coroutineHandler(fn: suspend (RoutingContext) -> Unit) {
        handler { ctx ->
            launch(ctx.vertx().dispatcher()) {
                try {
                    fn(ctx)
                } catch (e: Exception) {
                    ctx.fail(e)
                    logger.info("Catch exception", e)
                }
            }
        }
    }

    override fun onApplicationEvent(p0: ApplicationReadyEvent) {
        if (properties.enable) {
            val vertx = Vertx.vertx()!!
            vertx.deployVerticle(applicationContext.getBean(this::class.java))
            logger.info("Vertx deployed")
        }
    }
}