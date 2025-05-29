package com.example.multiplayerquizgame.util

import kotlinx.coroutines.*

class TimerService(private val scope: CoroutineScope) {

    companion object {
        val ANSWER_DURATION = 5000L
        val REVEAL_ANSWER_DURATION = 3000L
        val TICK_DURATION = 1000L
    }

    fun startAnswerTimer(task: suspend() -> Unit): Job {
        return startTimer(ANSWER_DURATION, task)
    }

    fun startRevealTimer(task: suspend() -> Unit): Job {
        return startTimer(REVEAL_ANSWER_DURATION, task)
    }

    private fun startTimer(duration: Long, task: suspend() -> Unit): Job {
        return scope.launch {
            delay(duration)
            task()
        }
    }

}