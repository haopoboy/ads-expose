package io.github.haopooby.config

import io.github.haopooby.Profiles
import io.github.haopooby.Utils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer

@Profile(Profiles.REDIS)
@Configuration
class RedisConfig {

    @Bean
    fun config() = RedisCacheConfiguration.defaultCacheConfig()
            .serializeValuesWith(fromSerializer(
                    // Use default object mapper to avoid get() error
                    GenericJackson2JsonRedisSerializer(Utils.getObjectMapper()))
            )

    @Bean
    fun template(redisConnectionFactory: RedisConnectionFactory) = RedisTemplate<String, Any>().apply {
        this.setConnectionFactory(redisConnectionFactory)
        this.setDefaultSerializer(GenericJackson2JsonRedisSerializer(Utils.getObjectMapper()))
    }
}