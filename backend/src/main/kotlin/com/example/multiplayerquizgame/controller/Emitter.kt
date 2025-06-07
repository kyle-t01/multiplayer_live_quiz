package com.example.multiplayerquizgame.controller

import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.model.GameEventType
import com.example.multiplayerquizgame.model.Lobby
import com.example.multiplayerquizgame.model.Player
import com.example.multiplayerquizgame.redis.RedisMessagePublisher
import com.example.multiplayerquizgame.util.JsonMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class Emitter(private val mapper: JsonMapper,
              private val lobby: Lobby,
              private val publisher: RedisMessagePublisher
)
{
    // send data to sessions
    /**
     * Emit signal to a player
     *
     * @param session
     * @param event
     */
    fun emit(session: WebSocketSession, event: GameEvent) {
        val msg = mapper.convertToTextMessage(event)
        session.sendMessage(msg)
    }

    /**
     * Emit to all session of a list
     *
     * @param sessionList
     * @param event
     */
    fun emitToAll(sessionList: List<WebSocketSession>, event: GameEvent) {
        for (s in sessionList){
            emit(s, event)
        }
    }

    /**
     * Emit to all players
     *
     * Emit to local websocket session, else use redis pub/sub
     *
     * @param players
     * @param event
     */
    fun emitToAllPlayers(players: List<Player>, event: GameEvent){
        for (p in players) {
            val s = lobby.getSessionFromPlayer(p)
            if (s != null) {
                emit(s, event)
                continue
            }
            // can not find session, must be external player
            println("session not found, using redis pub/sub")
            //publisher.publish(event)
        }

    }

    // emit to a player (whether local or external)
    fun emitToPlayer(player: Player, event: GameEvent) {
        val session = lobby.getSessionFromPlayer(player)
        if (session != null) {
            // handle local player via websocket session
            emit(session, event)
            return
        }
        // handle external player via redis pub/sub
        publisher.publishToPlayer(player.getID(), event)

    }

}