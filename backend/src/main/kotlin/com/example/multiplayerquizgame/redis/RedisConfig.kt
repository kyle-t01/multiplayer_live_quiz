package com.example.multiplayerquizgame.redis

import com.example.multiplayerquizgame.log.Logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.*
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import java.net.Socket

@Configuration
class RedisConfig {
    @Bean
    fun connectionFactory(): LettuceConnectionFactory {
        return LettuceConnectionFactory(HOST, PORT)
    }

    @Bean
    fun template(factory: RedisConnectionFactory): RedisTemplate<String, Any>? {
        // redis check
        if (!haveRedisConnection()) {
            println("skipping redis template")
            return null
        }

        val template = RedisTemplate<String, Any>()
        template.connectionFactory = factory
        template.setDefaultSerializer(GenericJackson2JsonRedisSerializer())
        return template
    }
    // broadcast to other servers
    @Bean
    fun serverBroadcastTopic() = PatternTopic("server-broadcast:*")

    @Bean
    fun messageListenerAdapter(subscriber: RedisMessageSubscriber): MessageListener = MessageListenerAdapter(subscriber)

    @Bean
    fun redisContainer(
        connectionFactory: RedisConnectionFactory,
        messageListener: MessageListenerAdapter,
        @Qualifier("serverBroadcastTopic") serverBroadcastTopic: PatternTopic,
    ): RedisMessageListenerContainer? {
        if (!haveRedisConnection()) {
            return null
        }

        return try {
            val container = RedisMessageListenerContainer();
            container.setConnectionFactory(connectionFactory)
            container.addMessageListener(messageListener, serverBroadcastTopic)
            container
        } catch (e: Exception) {
            Logger.logPod(null, "redis pub/sub down: $e")
            null
        }
    }

    companion object {
        val HOST = System.getenv("SPRING_REDIS_HOST") ?: "localhost"
        val PORT: Int = System.getenv("SPRING_REDIS_PORT")?.toIntOrNull() ?: 6379
    }

    private fun haveRedisConnection(): Boolean {
        return try {
            Socket(HOST, PORT).use { true }
        } catch (e: Exception) {
            Logger.logPod(null, "redis pub/sub down: $e")
            false
        }
    }
}