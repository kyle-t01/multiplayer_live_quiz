package com.example.multiplayerquizgame.model

import java.awt.desktop.QuitStrategy

val q1 = Question("What is the FIRST LAW OF ROBOTICS according to Isaac Asimov?",
    options = listOf(
        "A robot must obey the orders given it by human beings",
        "A robot must protect its own existence",
        "A robot may not injure a human being or, through inaction, allow a human being to come to harm.",
        "A robot may not injure humanity or, through inaction, allow humanity to come to harm."
    ),
    answers = listOf(2)
)

val q2 = Question(
    question = "The Cylons (a race of sentient AI-driven robots) originate from WHICH sci-fi series?",
    options = listOf("Doctor Who", "Star Wars", "Battlestar Galactica", "The Expanse"),
    answers = listOf(2)
)

val q3 = Question(
    question = "The Borg (a hive-mind cybernetic species that assimilates others) are from WHICH universe?",
    options = listOf("Star Trek", "Stargate", "Mass Effect", "The Matrix"),
    answers = listOf(0)
)

val q4 = Question(
    question = "The supercomputer AM (Allied Mastercomputer, Adaptive Manipulator, Aggressive Menace) is from which sci-fi novel?",
    options = listOf(
        "The Expanse",
        "I Have No Mouth, and I Must Scream",
        "Blade Runner",
        "Do Androids Dream of Electric Sheep?"
    ),
    answers = listOf(1)
)

val q5 = Question(
    question = "What is the NAME of the elite cybercrime unit in Ghost in the Shell?",
    options = listOf("Section 31", "NERV", "The Division","Public Security Section 9"),
    answers = listOf(3)
)

val q6 = Question(
    question = "The 2023 Netflix anime PLUTO is a reimagining of WHICH classic anime?",
    options = listOf("Astro Boy", "Akira", "Ghost in the Shell", "Evangelion"),
    answers = listOf(0)
)

val q7 = Question(
    question = "In NieR:Automata Ver1.1a, WHAT is the official slogan of YoRHa android units?",
    options = listOf(
        "Resistance is Futile",
        "Glory to Mankind",
        "Secure, Contain, Protect",
        "The Spice Must Flow"
    ),
    answers = listOf(1)
)

class Quiz() {
    private val questionList: MutableList<Question> = mutableListOf()
    private var currentIndex: Int = 0


    companion object {
        fun createQuizDefault(): Quiz {
            val quiz = Quiz()
            quiz.questionList.addAll(mutableListOf(q1, q2, q3, q4, q5, q6, q7))
            quiz.currentIndex = 0
            return quiz
        }
    }

    // end Quiz, reset to default state
    fun endQuiz(){
        questionList.clear()
        currentIndex = 0
    }

    // have we finished all questions in the quiz
    fun isFinished():Boolean {
        return (currentIndex >= questionList.size)
    }


    // get current question, isFinished() must be called beforehand
    fun getCurrentQ():Question {
        require(questionList.size > 0) {"ERROR: no questions!"}
        val q = questionList[currentIndex]
        println("Current question: ${q.question}")
        return q
    }

    // get current answer(s)
    fun getCurrentA():List<Int> {
        return questionList[currentIndex].answers
    }

    // prepare the next question (increments index by 1), return false if no more questions
    fun prepareNextQ(): Boolean {
        currentIndex += 1
        if (isFinished()) return false
        return true
    }


}