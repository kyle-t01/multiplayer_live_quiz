package com.example.multiplayerquizgame.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

    @Component
    class RedisMessagePublisher(
        private val redisTemplate: RedisTemplate<String, Any>?,
    ) {

        /**
         * Publish to all (server-broadcast:<type>)
         *
         * @param message
         */
        fun publishToAll(message: String) {
            if (redisTemplate == null) {
                println("redis pub/sub offline, can not publish")
                return
            }


            val topic = "$SERVER_BROADCAST_TOPIC:all"
            println("publishing to [$topic] with [$message]")
            redisTemplate.convertAndSend(topic, message)
        }

        /**
         * Publish to gateway
         *
         * ie, server-broadcast:pong => backend-deployment-0
         *
         * @param message
         */
        fun publishToGateway(message: String) {
            if (redisTemplate == null) {
                println("redis pub/sub offline, can not PONG")
                return
            }
            val topic = "$SERVER_BROADCAST_TOPIC:pong"
            println("publishing to [$topic] with [$message]")
            redisTemplate.convertAndSend(topic, message)
        }

        companion object {
            val SERVER_BROADCAST_TOPIC = "server-broadcast"
        }

    }