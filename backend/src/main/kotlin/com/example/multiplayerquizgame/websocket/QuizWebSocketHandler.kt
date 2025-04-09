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
        lobby.removePlayer(session)
        emitToAllLobbyUpdate()
    }

    // handle game events
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val json = mapper.readTree(message.payload)
        // json = { type: "", data: {} }
        val typeStr:String = json.get("type").asText().uppercase()
        val type = GameEventType.valueOf(typeStr)
        val data = json.get("data").asText()

        // print game events sent by players to terminal
        // println("$type: $data")

        when (type) {
            GameEventType.JOIN -> {
                val player = Player(data)
                lobby.addToPlayers(session, player)
                // did this player join when the game already started?
                if (lobby.isGameStarted) {
                    // then KICK the player
                    println("Kicking ${player.name} from game.")
                    emit(session, GameEvent(GameEventType.KICK, player))
                    return
                }
                // signal to the player, of successful JOIN
                emit(session,GameEvent(GameEventType.JOIN, player))
                // update the lobby of all players
                emitToAllLobbyUpdate()
            }
            GameEventType.START -> {
                if (lobby.isGameStarted) {
                    // game already started
                    return
                }
                lobby.startGame()
               // get the first question
                val q = lobby.quiz.getCurrentQ()
                println("Current question: ${q.question}")

                // signal to all players, that the Game has Started, and the first question!
                emitToAll(GameEvent(GameEventType.START, q))
            }
            GameEventType.ANSWER -> {
                // extract message
                val ans:Int = data.toInt()

                // validate answer
                lobby.validateAnswer(session, ans)

                // tell player the actual correct answer(s)
                emit(session, GameEvent(GameEventType.ANSWER, lobby.quiz.getCurrentA()))

                // broadcast state change to everyone
                emitToAllLobbyUpdate()

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

    // helper signal to perform a lobby update
    private fun emitToAllLobbyUpdate() {
        emitToAll(GameEvent(GameEventType.LOBBY_UPDATE, lobby.getPlayers()))
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