package com.example.multiplayerquizgame.model

data class Quiz(
    val questionList: List<Question> = listOf(),
    var currentIndex: Int = 0,

)