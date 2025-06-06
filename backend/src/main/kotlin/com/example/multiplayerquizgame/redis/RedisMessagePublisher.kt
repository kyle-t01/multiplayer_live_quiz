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
         * Publish to room
         *
         * @param roomCode
         * @param event
         */
        fun publishToRoom(roomCode: String, event: GameEvent) {
            val topic = "game-room:$roomCode"
            println("publishing to [$topic]")
            redisTemplate.convertAndSend(topic, event)
        }

        /**
         * Publish to player
         *
         * @param playerID
         * @param event
         */
        fun publishToPlayer(playerID: String, event: GameEvent) {
            val topic = "player-id:$playerID"
            println("publishing to [$topic]")
            redisTemplate.convertAndSend(topic, event)
        }
    }