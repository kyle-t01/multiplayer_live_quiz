package com.example.multiplayerquizgame.model

data class GameEvent(
    val type: GameEventType,
    val data: Any, // allow for any type of object
)
