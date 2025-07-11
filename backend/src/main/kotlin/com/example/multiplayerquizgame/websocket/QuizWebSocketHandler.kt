package com.example.multiplayerquizgame.websocket

// models:
import com.example.multiplayerquizgame.controller.Emitter
import com.example.multiplayerquizgame.controller.GameLoopController
import com.example.multiplayerquizgame.controller.GameSessionController
import com.example.multiplayerquizgame.log.Logger
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


class QuizWebSocketHandler (
    private val mapper:JsonMapper,
    private val gameController: GameSessionController
) : TextWebSocketHandler() {

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
        // println("$type: $data")

        // have gameLoop handle game events messages
        gameController.handleInternalGameEventTraffic(session, gameEvent)
    }
}

@Configuration
@EnableWebSocket
class WSConfig(
    private val mapper:JsonMapper,
    private val gameController: GameSessionController
): WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        val path = getPath()
        registry
            .addHandler(QuizWebSocketHandler(mapper, gameController), path)
            .setAllowedOrigins("*")
    }

    private fun getPath(): String {
        val podName = System.getenv("HOSTNAME") ?: ""
        val idx = podName.substringAfterLast("-")
        // determine path based on podName
        var path = ""
        if (podName.startsWith("backend")) {
            // expected path
            path = "/quiz/$idx"
        } else {
            // unexpected, use default path
            path = "/quiz"
        }
        println("[WSConfig] name: $podName, path: $path")
        return path
    }
}