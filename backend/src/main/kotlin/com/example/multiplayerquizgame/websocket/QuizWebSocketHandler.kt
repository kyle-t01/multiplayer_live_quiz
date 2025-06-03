package com.example.multiplayerquizgame.websocket

// models:
import com.example.multiplayerquizgame.controller.Emitter
import com.example.multiplayerquizgame.controller.GameLoopController
import com.example.multiplayerquizgame.controller.GameSessionController
import com.example.multiplayerquizgame.model.*
import com.example.multiplayerquizgame.util.JsonMapper
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.handler.TextWebSocketHandler


class QuizWebSocketHandler (private val mapper:JsonMapper, private val lobby: Lobby, private val emitter: Emitter) : TextWebSocketHandler() {

    private val gameController: GameSessionController = GameSessionController(lobby, emitter)

    // remove player from lobby on disconnect
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        gameController.handleConnectionClosed(session)
    }

    // handle game events
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val gameEvent:GameEvent = mapper.readTextMessage(message)
        // json = { type: "", data: {} }
        val type = gameEvent.type
        val data = gameEvent.data

        // print game events sent by players to terminal
        println("$type: $data")

        // have gameLoop handle game events messages
        gameController.handleGameEventTraffic(session, gameEvent)
    }
}

@Configuration
@EnableWebSocket
class WSConfig(private val mapper:JsonMapper, private val lobby: Lobby, private val emitter: Emitter): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry
            .addHandler(QuizWebSocketHandler(mapper, lobby, emitter), "/quiz")
            .setAllowedOrigins("*")
    }
}