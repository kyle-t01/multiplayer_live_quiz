package com.example.multiplayerquizgame.websocket

// models:
import com.example.multiplayerquizgame.model.*

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
        val player= lobby.players[session]
        println("$player LEFT the game!")
        lobby.players.remove(session)
        // update the lobby
        // signal to all players, the updated lobby
        emitToAll(GameEvent(GameEventType.LOBBY_UPDATE, lobby.players.values))

        // if there are no more players, then game has ended
        if (lobby.players.isEmpty()) {
            lobby.isGameStarted = false;
        }

    }

    // handle game events
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val json = mapper.readTree(message.payload)
        val type = json.get("type").asText()
        when (GameEventType.valueOf(type.uppercase())) {
            GameEventType.JOIN -> {
                val name = json.get("data").asText()
                val player = Player(name)
                println("${player.name} JOINED THE LOBBY")

                // associate the session with this player
                lobby.players[session] = player

                // did this player join when the game already started?
                if (lobby.isGameStarted) {
                    // then KICK the player
                    emit(session, GameEvent(GameEventType.KICK, player))
                    return
                }

                // signal to the player, of successful JOIN
                emit(session,GameEvent(GameEventType.JOIN, player))

                // signal to all players, the updated lobby
                val playerList = lobby.players.values
                emitToAll(GameEvent(GameEventType.LOBBY_UPDATE, playerList))
            }
            GameEventType.START -> {

                if (lobby.isGameStarted) {
                    // game already started
                    println("Game has already started!")
                    return
                }
                // start the game
                println("Game has started!")
                lobby.isGameStarted = true

                // load the quiz questions and get the first question
                val q = lobby.quiz.loadQuiz()

                // signal to all players, that the Game has Started, and the first question!
                emitToAll(GameEvent(GameEventType.START, q))
            }
            GameEventType.ANSWER -> {
                // to implement
                val ans = json.get("data").asText()
                println("You tried to answer with $ans")
            }
            GameEventType.LEAVE -> {
                println("Unexpected Usage! - The only way to leave a lobby is to close the page!")
            }
            GameEventType.LOBBY_UPDATE -> {
                println("Unexpected Usage! - Client shouldn't need to ask for lobby updates!")
            }
            GameEventType.KICK -> {
                println("Unexpected Usage! - KICK is not implemented!")
            }
        }
    }
    // emit signal to player
    private fun emit(session: WebSocketSession, event: GameEvent) {
        val json = mapper.writeValueAsString(event)
        session.sendMessage(TextMessage(json))
    }

    // emit signal to all players in lobby
    private fun emitToAll(event: GameEvent) {
        for (s in lobby.players.keys) {
            emit(s, event)
        }
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