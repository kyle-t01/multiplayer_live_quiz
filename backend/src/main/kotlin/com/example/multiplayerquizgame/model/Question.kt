package com.example.multiplayerquizgame.model

data class Question(
    val question: String,
    val options: List<String>,
    val answers: List<Int>,  // index of correct answers
    // val image: String,
)