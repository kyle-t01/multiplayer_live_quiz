package com.example.multiplayerquizgame.model

import org.springframework.web.socket.WebSocketSession
class Lobby(
    val players: MutableMap<WebSocketSession, Player> = mutableMapOf(),
) {

    // remove a <session, player> from lobby on disconnect
    fun removePlayer(session: WebSocketSession) {
        val player= players[session]
        println("$player LEFT the MAIN LOBBY! [${players.size -1} players left...]")
        players.remove(session)
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
        println("$player JOINED the game! [${players.size} players]")
        return
    }

    fun getPlayerFromSession(session: WebSocketSession): Player? {
        return players[session]
    }


}