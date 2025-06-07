package com.example.multiplayerquizgame.model
import java.util.UUID


data class Player(
    val name: String,
    var qCorrect: Int = 0,
    var roomCode: String
) {
    private val id: String = UUID.randomUUID().toString()

    fun resetScore() {
        qCorrect = 0
    }

    fun getID() = id
}