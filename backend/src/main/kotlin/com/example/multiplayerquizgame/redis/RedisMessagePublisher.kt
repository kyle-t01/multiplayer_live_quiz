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
         * Publish to room (player-room communication)
         *
         * @param roomCode
         * @param event
         */
        fun publishToRoom(playerID: String, roomCode: String, event: GameEvent) {
            val topicPrefix = "player-room"
            val topic = "$topicPrefix:$playerID:$roomCode"
            println("publishing to [$topic]")
            redisTemplate.convertAndSend(topic, event)
        }

        /**
         * Publish to player (room-player communication)
         *
         * @param playerID
         * @param event
         */
        fun publishToPlayer(roomCode: String, playerID: String, event: GameEvent) {
            val topicPrefix = "room-player"
            val topic = "room-player:$roomCode:$playerID"
            println("publishing to [$topic]")
            redisTemplate.convertAndSend(topic, event)
        }
    }