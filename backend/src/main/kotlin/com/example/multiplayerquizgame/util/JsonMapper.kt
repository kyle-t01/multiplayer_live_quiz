package com.example.multiplayerquizgame.util

import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.model.GameEventType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage

/* singleton class that manages conversion between Kotlin objects <-> JSON <-> TextMessage */
@Component
class JsonMapper {
    private val mapper = jacksonObjectMapper()

    // helper function to parse Text Message to GameEvent
    fun readTextMessage(message: TextMessage): GameEvent {
        val json = mapper.readTree(message.payload)
        // json = { type: "", data: {} }
        val typeStr:String = json.get("type").asText().uppercase()
        val type = GameEventType.valueOf(typeStr)
        val data = json.get("data")
        return  GameEvent(type, data)
    }

    // convert gameEvent to TextMessage
    fun convertToTextMessage(gameEvent: GameEvent) : TextMessage {
        val json = mapper.writeValueAsString(gameEvent)
        return TextMessage(json)
    }
}