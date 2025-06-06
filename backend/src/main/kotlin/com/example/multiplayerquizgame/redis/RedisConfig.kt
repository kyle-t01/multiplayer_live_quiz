package com.example.multiplayerquizgame.redis

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.*
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.PatternTopic
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
    // from player to game-room
    @Bean
    fun gameRoomPattern() = PatternTopic("game-room:*")

    // from room to player
    @Bean
    fun playerPattern() = PatternTopic("player-id:*")



    @Bean
    fun messageListenerAdapter(subscriber: RedisMessageSubscriber): MessageListener = MessageListenerAdapter(subscriber)

    @Bean
    fun redisContainer(
        connectionFactory: RedisConnectionFactory,
        messageListener: MessageListenerAdapter,
        @Qualifier("gameRoomPattern") roomPatternTopic: PatternTopic,
        @Qualifier("playerPattern") playerPatternTopic: PatternTopic
    ): RedisMessageListenerContainer {
        val container: RedisMessageListenerContainer = RedisMessageListenerContainer();
        container.apply {
            setConnectionFactory(connectionFactory)
            addMessageListener(messageListener, roomPatternTopic)
            addMessageListener(messageListener, playerPatternTopic)
        }
        return container
    }

}