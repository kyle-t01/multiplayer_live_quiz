package com.example.multiplayerquizgame.model
import com.example.multiplayerquizgame.log.Logger
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class Lobby(
    val players: MutableMap<WebSocketSession, Player> = mutableMapOf(),
    val logger: Logger
) {

    // remove a <session, player> from lobby on disconnect
    fun removePlayer(session: WebSocketSession): Player? {
        val player= players[session]
        logger.logWS(null, "$player LEFT the MAIN LOBBY! [${players.size -1} players left...]")
        return players.remove(session)
    }

    // return a List<Player> currently in the lobby
    fun getPlayers():List<Player> {
        return players.values.toList()
    }

    // add a <session, player> pair to players
    fun addToPlayers(session: WebSocketSession, player: Player) {
        // associate the session with this player
        players[session] = player
        logger.logWS(null, "$player JOINED the game! [${players.size} players]")
        return
    }

    fun getPlayerFromSession(session: WebSocketSession): Player? {
        return players[session]
    }

    fun getSessionFromPlayer(player: Player): WebSocketSession? {
        return players.entries.find{ it.value == player}?.key
    }

    fun getRoomCodeFromSession(session: WebSocketSession): String? {
        val player = getPlayerFromSession(session)
        val code = player?.roomCode
        return code
    }

    /**
     * Get player by ID
     *
     * @param id
     * @return
     */
    fun getPlayerByID(id: String): Player? {
        val player = getPlayers().find{ it.getID() == id }
        return player
    }

}