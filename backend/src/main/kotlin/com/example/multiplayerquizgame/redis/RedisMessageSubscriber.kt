package com.example.multiplayerquizgame.redis

import com.example.multiplayerquizgame.controller.GameSessionController
import com.example.multiplayerquizgame.util.JsonMapper
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class RedisMessageSubscriber(
    private val mapper: JsonMapper,
    private val gameSessionController: GameSessionController
): MessageListener{

    /**
     * On message
     *
     * have GameSessionController handle signals
     *
     * @param message
     * @param pattern
     */
    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            // ie: game-room:1234
            val topic = message.channel.decodeToString()
            // ie: raw data
            val bodyMessage = message.body.decodeToString()
            println("this was the message received: [$topic]:$bodyMessage")
            // extract topic
            val topicParts = getTopicParts(topic)
            val prefix = topicParts[0]
            val id = topicParts[1]
            // handle body
            val gameEvent = mapper.convertStrToGameEvent(bodyMessage)
            println("gameEvent: $gameEvent")
            // handle message
            when (prefix) {
                "game-room" -> gameSessionController.handleRedisRoomEvent(id,gameEvent)
                "player-id" -> gameSessionController.handleRedisPlayerEvent(id, gameEvent)
                else -> println("unknown $prefix")
            }



        }catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }

    private fun getTopicParts(topic: String): List<String> {
        val parts = topic.split(":")
        if (parts.size != 2) {
            println("topic format invalid $topic")
            return listOf("","")
        }
        return parts
    }

}