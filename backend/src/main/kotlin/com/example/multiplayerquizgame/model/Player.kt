package com.example.multiplayerquizgame.model

data class Player(
    val name: String,
    var qCorrect: Int = 0,
    var roomCode: String
) {
    fun resetScore() {
        qCorrect = 0
    }
    
}