package com.example.multiplayerquizgame.model

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class Lobby(
    val players: MutableMap<WebSocketSession, Player> = mutableMapOf(),
    val quiz: Quiz = Quiz(),
    var isGameStarted: Boolean = false,

) {

    // remove a <session, player> from lobby on disconnect
    fun removePlayer(session: WebSocketSession) {
        val player= players[session]
        println("$player LEFT the game!")
        players.remove(session)

        // if there are no more players, then the game has ended
        if (players.isEmpty()) {
            isGameStarted = false;
        }
        return
    }

    // return a List<Player> currently in the lobby
    fun getPlayers():List<Player> {
        return players.values.toList()
    }

    // add a <session, player> pair to players
    fun addToPlayers(session: WebSocketSession, player: Player) {
        // associate the session with this player
        players[session] = player
        return
    }

    // start the game
    fun startGame() {
        println("Game has officially started.")
        isGameStarted = true
    }

}