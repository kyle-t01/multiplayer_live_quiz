package com.example.multiplayerquizgame.controller

import com.example.multiplayerquizgame.model.Game
import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.model.GameEventType
import com.example.multiplayerquizgame.model.Lobby
import org.springframework.web.socket.WebSocketSession

class GameSessionController (private val lobby: Lobby, private val emitter: Emitter) {

    // goal of class is to
    // create and delete new games
    // handle any signals to games
    // manage a list of games
    val games: MutableList<GameLoopController> = mutableListOf()

    fun createGame() {
        if (games.size >= MAX_GAMES) {
            // EXCEED max games
            println("Too many games, currently ${games.size}")
        }
        // else, add the game
        val game = GameLoopController(lobby, emitter)
        games.add(game)
        return
    }

    private fun findGameRoomFromSession(session: WebSocketSession): GameLoopController? {
        val roomCode = lobby.getRoomCodeFromSession(session) ?: return null
        return games.find {it.getRoomCode() == roomCode}
    }

    fun handleGameEventTraffic(session: WebSocketSession, gameEvent: GameEvent) {
        // are we creating a new room?
        val type = gameEvent.type
        if (type == GameEventType.CREATE) {
            // yes, so create new room
            createGame()
        }

        // find roomCode associated with this session
        val game = findGameRoomFromSession(session)
        if (game == null) {
            println("could not associate session with game!")
            return
        }
        // otherwise, redirect game event to the game
        game.handleGameEvent(session, gameEvent)
        return

    }

    fun handleConnectionClosed(session: WebSocketSession) {
        val game = findGameRoomFromSession(session)
        require(game != null) {"There MUST be a game where session is disc. from!"}
        // delegate to game loop
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