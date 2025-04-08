package com.example.multiplayerquizgame.model

data class GameEvent(
    val type: GameEventType,
    val data: String, // assume a GameEvent always has a string associated with it
)
