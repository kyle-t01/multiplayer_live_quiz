package com.example.multiplayerquizgame.controller

import com.example.multiplayerquizgame.model.Game
import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.model.GameEventType
import com.example.multiplayerquizgame.model.Lobby
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class GameSessionController (private val lobby: Lobby, private val emitter: Emitter) {

    // goal of class is to
    // create and delete new games
    // handle any signals to games
    // manage a list of games
    val games: MutableList<GameLoopController> = mutableListOf()

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

    fun handleGameEventTraffic(session: WebSocketSession, gameEvent: GameEvent) {
        // when creating or joining a game, not possible to associate session with game
        val type = gameEvent.type
        val jsonData = gameEvent.data as JsonNode
        var game: GameLoopController? = null
        when(type) {
            GameEventType.CREATE -> {
                // create game
                game = createGame()
            }
            GameEventType.JOIN -> {
                // find game by roomCode
                val roomCode = jsonData.get("roomCode")?.asText()
                game = findGameRoomFromRoomCode(roomCode)
                // can't find game, then KICK
                if (game == null) {
                    println("Room code $roomCode did not match any games!")
                    emitter.emit(session, GameEvent(GameEventType.KICK, ""))
                }
            }
            else -> {
                // find game by session
                game = findGameRoomFromSession(session)
            }
        }
        // could we find the game?
        if (game == null) {
            // no, just return
            return
        }
        // otherwise, handle game event
        game.handleGameEvent(session, gameEvent)
        return

    }

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

    companion object {
        val MAX_GAMES = 3
    }



}