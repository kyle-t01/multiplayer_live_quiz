package com.example.multiplayerquizgame.websocket

// models:
import com.example.multiplayerquizgame.model.*

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.*
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
    /* TODO: could benefit from moving coroutine logic to GameController */
    // scope to hold coroutines
    private val gameLoopScope = CoroutineScope(CoroutineName("GameLoopScope"))

    // track gameLoop coroutine
    private var gameLoopJob: Job? = null

    // remove player from lobby on disconnect
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        lobby.removePlayer(session)
        emitToAllLobbyUpdate()

        // when all players have disconnected, stop the gameLoopJob
        if (lobby.players.isEmpty()) {
            gameLoopJob?.cancel()
            gameLoopJob = null
        }
    }

    // handle game events
    @OptIn(DelicateCoroutinesApi::class)
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
                if (lobby.isGameStarted || gameLoopJob?.isActive == true) {
                    // then KICK the player
                    println("Kicking ${player.name} from game.")
                    emit(session, GameEvent(GameEventType.KICK, ""))
                    return
                }
                // signal to the player, of successful JOIN
                emit(session,GameEvent(GameEventType.JOIN, player))
                // update the lobby of all players
                emitToAllLobbyUpdate()
            }
            GameEventType.START -> {
                if (lobby.isGameStarted || gameLoopJob?.isActive == true) {
                    // game already started
                    return
                }
                lobby.startGame()
                // update the initial score of all players
                emitToAllLobbyUpdate()

                gameLoopJob = gameLoopScope.launch {
                    println("Launched Coroutine")
                    val answeringDuration:Long = 5000 // each Q has a 10s timer (that may be varied in the future)
                    val revealAnswerDuration:Long = 3000 // reveal answers for 5s before moving on
                    val updateDuration:Long = 1000 // update every 1000ms
                    try {
                        // tell all players game has started!
                        emitToAll(GameEvent(GameEventType.START, ""))
                        // while we have questions
                        while (!lobby.quiz.isFinished()) {

                            if (lobby.players.isEmpty()) {
                                // if no players, exit co-routine
                                return@launch
                            }
                            // get current question
                            val q = lobby.quiz.getCurrentQ()
                            // send it to all players
                            emitToAll(GameEvent(GameEventType.QUESTION, q))


                            var t:Long = 0
                            while(t < answeringDuration) {
                                // give time to players to answer questions
                                emitToAll(GameEvent(GameEventType.TIME, answeringDuration-t))
                                delay(updateDuration)
                                t += updateDuration
                            }
                            // timer has finished
                            emitToAll(GameEvent(GameEventType.TIME, 0))
                            // reveal answer to all players
                            emitToAll(GameEvent(GameEventType.SHOW, q.answers))
                            // give time to players to view answers
                            delay(revealAnswerDuration)
                            // increment the current question index
                            lobby.quiz.currentIndex += 1
                        }
                    } finally {
                        // any time co-routine exits (or when gameLoop needs to end)
                        println("Exiting Coroutine")
                        lobby.endGame()
                        gameLoopJob = null
                        emitToAll(GameEvent(GameEventType.END, ""))
                    }
                }
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
            else -> {
                println("Unexpected Usage of $type !")
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