package com.example.multiplayerquizgame.model

data class Player(
    val name: String,
    var qCorrect: Int = 0,
) {
    fun resetScore() {
        qCorrect = 0
    }
}