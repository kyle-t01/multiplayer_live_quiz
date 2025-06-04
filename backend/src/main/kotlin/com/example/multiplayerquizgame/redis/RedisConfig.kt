package com.example.multiplayerquizgame.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.*
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

@Configuration
class RedisConfig {
    @Bean
    fun connectionFactory()= LettuceConnectionFactory("localhost", 6379)

    @Bean
    fun template(factory: RedisConnectionFactory): RedisTemplate<String, Any>{
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = factory
        template.setDefaultSerializer(GenericJackson2JsonRedisSerializer())
        return template
    }

    @Bean
    fun topic() = ChannelTopic("game-room")

    @Bean
    fun messageListenerAdapter(subscriber: RedisMessageSubscriber): MessageListener = MessageListenerAdapter(subscriber)

    @Bean
    fun redisContainer(
        connectionFactory: RedisConnectionFactory,
        messageListener: MessageListenerAdapter,
        topic: ChannelTopic
    ): RedisMessageListenerContainer {
        val container: RedisMessageListenerContainer = RedisMessageListenerContainer();
        container.apply {
            setConnectionFactory(connectionFactory)
            addMessageListener(messageListener, topic)
        }
        return container
    }

}