package com.example.multiplayerquizgame.model

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class Lobby(
    val players: MutableMap<WebSocketSession, Player> = mutableMapOf(),
    val quiz: Quiz = Quiz(),
    var isGameStarted: Boolean = false,
)