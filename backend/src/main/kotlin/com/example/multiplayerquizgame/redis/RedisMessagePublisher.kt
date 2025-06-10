package com.example.multiplayerquizgame.redis

import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.util.JsonMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.PatternTopic
import org.springframework.stereotype.Component

    @Component
    class RedisMessagePublisher(
        private val redisTemplate: RedisTemplate<String, Any>,
    ) {

        /**
         * Publish to all (server-broadcast:<type>)
         *
         * @param message
         */
        fun publishToAll(message: String) {
            val topic = "$SERVER_BROADCAST_TOPIC:<all>"
            println("publishing to [$topic] with [$message]")
            redisTemplate.convertAndSend(topic, message)
        }
        companion object {
            val SERVER_BROADCAST_TOPIC = "server-broadcast"
        }

    }