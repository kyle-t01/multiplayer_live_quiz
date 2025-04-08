package com.example.multiplayerquizgame.model

data class Quiz(
    val questionList: MutableList<Question> = mutableListOf(),
    var currentIndex: Int = 0,

    )