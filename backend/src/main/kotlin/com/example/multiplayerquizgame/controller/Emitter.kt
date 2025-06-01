package com.example.multiplayerquizgame.controller

import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.model.GameEventType
import com.example.multiplayerquizgame.util.JsonMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class Emitter(private val mapper: JsonMapper) {
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

}