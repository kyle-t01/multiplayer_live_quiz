package com.example.multiplayerquizgame.websocket

// models
import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.model.GameEventType
import com.example.multiplayerquizgame.model.Lobby
import com.example.multiplayerquizgame.model.Player
import com.example.multiplayerquizgame.model.Quiz

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

import org.springframework.web.socket.handler.TextWebSocketHandler

class QuizWebSocketHandler (private val lobby: Lobby) : TextWebSocketHandler(){
    // allow conversion of Kotlin objects <-> JSON,
    private val mapper = jacksonObjectMapper()

    // remove player from lobby on disconnect
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        lobby.players.remove(session)
    }

    // handle game events
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val json = mapper.readTree(message.payload)
        val type = json.get("type").asText()
        when (GameEventType.valueOf(type.uppercase())) {
            GameEventType.JOIN -> {
                val name = json.get("data").asText()
                val player = Player(name)
                lobby.players[session] = player
                val playerList = lobby.players.values
                println("WEB SOCKET CONNECTED, PLAYER JOINED")
                emit(session, GameEvent(GameEventType.JOIN, playerList.toString()))
            }
            GameEventType.ANSWER -> {
                val ans = json.get("data").asText()
                println("You tried to answer with $ans")
            }
            GameEventType.LEAVE -> {
                println("YOU LEFT THE GAME")
                // disconnect
            }
        }
    }
    // emit signal to client
    private fun emit(session: WebSocketSession, event: GameEvent) {
        val json = mapper.writeValueAsString(event)
        session.sendMessage(TextMessage(json))
    }

}

@Configuration
@EnableWebSocket
class WSConfig(private val lobby: Lobby): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(QuizWebSocketHandler(lobby), "/quiz")
            .setAllowedOrigins("*")
    }
}