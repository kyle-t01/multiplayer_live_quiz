package com.example.multiplayerquizgame.model

data class Question(
    val question: String,
    val options: List<String>,
    val answers: List<String>,  // potentially more than one correct answer
    // val image: String,
)