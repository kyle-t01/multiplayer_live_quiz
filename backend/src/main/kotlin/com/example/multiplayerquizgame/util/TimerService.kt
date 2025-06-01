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

    /**
     * Start timer with tick
     *
     * @param duration
     * @param task
     * @receiver
     * @return
     */
    fun startTickingTimer(duration: Long, task: suspend() -> Unit, onTick: suspend(timeLeft: Long) -> Unit): Job {
        return scope.launch {
            var t: Long = duration
            while (t > 0) {
                onTick(t)
                delay(TICK_DURATION)
                t -= TICK_DURATION
            }
            task()
        }
    }

    /**
     * Start ticking answer timer
     *
     * @param task
     * @param onTick
     * @receiver
     * @receiver
     * @return
     */
    fun startTickingAnswerTimer(task: suspend() -> Unit, onTick: suspend(timeLeft: Long) -> Unit): Job {
        return startTickingTimer(ANSWER_DURATION, task, onTick)
    }

    private fun startTimer(duration: Long, task: suspend() -> Unit): Job {
        return scope.launch {
            delay(duration)
            task()
        }
    }

}