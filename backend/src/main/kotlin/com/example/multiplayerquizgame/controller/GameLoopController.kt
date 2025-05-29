package com.example.multiplayerquizgame.controller

import com.example.multiplayerquizgame.model.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.springframework.web.socket.WebSocketSession

class GameLoopController(private val lobby: Lobby,
                         private val game: Game,
                         private val gameLoopScope: CoroutineScope = CoroutineScope(CoroutineName("GameLoopScope"))
)
{
    // track gameLoop coroutine
    private var gameLoopJob: Job? = null

    fun handleGameEvent(session: WebSocketSession, gameEvent: GameEvent) {
        val type = gameEvent.type
        val data = gameEvent.data

        when(type) {
            GameEventType.JOIN -> handleJoin(session, gameEvent)
            else -> println("unexpected usage of ${gameEvent}!")
        }
    }

    /**
     * Handle join
     *
     * adds player to lobby and game
     *
     * @param session
     * @param gameEvent
     */
    fun handleJoin(session: WebSocketSession, gameEvent: GameEvent) {
        val type = gameEvent.type
        val data = gameEvent.data
        // for now, add player to lobby and game
        // TODO: player should JOIN via lobby ID
        val player = Player(data.toString())
        lobby.addToPlayers(session, player)
        game.addPlayer(player)

        // emit signals


    }

    // get current players
    fun getCurrentPlayers(): List<Player> {
        return game.getPlayers()
    }

    // get all sessions connected to this game
    fun getPlayerSessions(): List<WebSocketSession> {
        val players = getCurrentPlayers()
        val sessionList = players.mapNotNull{lobby.getSessionFromPlayer(it)}
        return sessionList
    }

}