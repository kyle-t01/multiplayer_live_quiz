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
            // ie: server-broadcast:all
            val topic = message.channel.decodeToString()
            // ie: raw data
            val bodyMessage = message.body.decodeToString()
            gameSessionController.handleExternalGameEventTraffic(topic, bodyMessage)

        }catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }
}