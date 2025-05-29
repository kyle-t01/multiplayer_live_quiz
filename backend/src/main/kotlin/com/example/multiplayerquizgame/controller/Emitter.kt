package com.example.multiplayerquizgame.controller

import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.util.JsonMapper
import org.springframework.web.socket.WebSocketSession

class Emitter(private val mapper: JsonMapper) {
    // send data to sessions
    /**
     * Emit signal to a player
     *
     * @param session
     * @param event
     */
    private fun emit(session: WebSocketSession, event: GameEvent) {
        val msg = mapper.convertToTextMessage(event)
        session.sendMessage(msg)
    }


    private fun emitToAll(event) {
        
    }
}