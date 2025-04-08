package com.example.multiplayerquizgame.model

data class Question(
    val question: String,
    val options: List<String>,
    val answers: Int,  // Index of singular correct answer
    // val image: String,
)