package com.example.multiplayerquizgame.controller

import com.example.multiplayerquizgame.model.*
import com.fasterxml.jackson.databind.JsonNode
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.util.*

@Service
class GameSessionController (private val lobby: Lobby, private val emitter: Emitter) {

    private val id:String = UUID.randomUUID().toString()
    val games: MutableList<GameLoopController> = mutableListOf()

    @EventListener(ApplicationReadyEvent::class)
    fun onInit() {
        // on init (after beans active), broadcast "server:<id> says HELLO-WORLD!"
        println("GameSessionController init...")
        val msg = id
        try {
            emitter.emitServerBroadcast(msg)
        } catch (e: Exception) {
            println("Redis emit failed: ${e.message}")
        }
    }


    fun createGame(): GameLoopController? {
        if (games.size >= MAX_GAMES) {
            // EXCEED max games
            println("Too many games, currently ${games.size}")
            return null
        }
        // else, add the game
        val game = GameLoopController(lobby, emitter)
        games.add(game)
        println("game ${game.getRoomCode()} created")
        return game
    }

    private fun findGameRoomFromSession(session: WebSocketSession): GameLoopController? {
        val roomCode = lobby.getRoomCodeFromSession(session) ?: return null
        return findGameRoomFromRoomCode(roomCode)
    }

    private fun findGameRoomFromRoomCode(roomCode: String?): GameLoopController? {
        if (roomCode == null || roomCode == "") return null
        return games.find {it.getRoomCode() == roomCode}
    }

    fun handleInternalGameEventTraffic(session: WebSocketSession, gameEvent: GameEvent) {
        // when creating or joining a game, not possible to associate session with game
        val type = gameEvent.type
        val jsonData = gameEvent.data as JsonNode
        var game: GameLoopController? = null
        var player: Player? = lobby.getPlayerFromSession(session)
        when(type) {
            GameEventType.CREATE -> {
                // create game
                game = createGame()
                // can't create game, then KICK (at full capacity)
                if (game == null) {
                    println("Server at full capacity!")
                    emitter.emit(session, GameEvent(GameEventType.KICK, ""))
                }
                // create player
                player = newPlayerFromGameEvent(gameEvent)
                lobby.addToPlayers(session, player)
            }
            GameEventType.JOIN -> {
                // find game by roomCode
                val roomCode = jsonData.get("roomCode")?.asText()
                game = findGameRoomFromRoomCode(roomCode)
                // can't find game, then KICK
                if (game == null) {
                    println("Room code $roomCode did not match any games!")
                    emitter.emit(session, GameEvent(GameEventType.KICK, ""))
                    return
                }
                player = newPlayerFromGameEvent(gameEvent)
                player.roomCode = game.getRoomCode()
                lobby.addToPlayers(session, player)
            }
            else -> {
                // find game by session
                game = findGameRoomFromSession(session)
            }
        }

        if (game == null || player == null) return
        // handle game event
        game.handleGameEvent(player, gameEvent)
        return
    }

    /**
     * Handle external game event traffic (from redis pub/sub events)
     *
     * @param topic
     * @param message
     */
    fun handleExternalGameEventTraffic(topic: String, message: String) {
        val topicParts = getTopicParts(topic)
        val prefix = topicParts[0]
        val type = topicParts[1]
        when(prefix) {
            "server-broadcast" -> {
                println("[$prefix:<$type>]: <$message> started up!")
            }
            else -> println("unknown topic: $prefix:<$type> => $message")
        }
    }

    // merge together
    // fun handleGameEventTraffic
    fun handleConnectionClosed(session: WebSocketSession) {
        // find game associated with session
        val game = findGameRoomFromSession(session)
        // is associated with a game?
        if (game == null) {
            // no, so do nothing
            return
        }
        // yes, then delegate disconnect to game loop
        game.handleDisconnect(session)
        // if there are no players in the game, can safely remove it
        if (game.hasNoPlayers()) {
            // no players, game needs to be removed
            println("game with ${game.getRoomCode()} removed due to empty lobby")
            games.remove(game)
        }
    }

    // get the components of topic
    private fun getTopicParts(topic: String): List<String> {
        val parts = topic.split(":")
        if (parts.size != 2) {
            println("topic format invalid $topic")
            return listOf("","")
        }
        return parts
    }

    /**
     * New player from game event
     *
     * @param gameEvent
     * @return
     */
    private fun newPlayerFromGameEvent(gameEvent: GameEvent): Player {
        // TODO: refactor this with mapper class
        val jsonData = gameEvent.data as JsonNode
        val name = jsonData.get("playerName")?.asText() ?: "Joining..."
        val code = jsonData.get("roomCode")?.asText() ?: ""
        return Player(name, 0, code)
    }

    fun getID()= id

    companion object {
        val MAX_GAMES = 3
    }



}