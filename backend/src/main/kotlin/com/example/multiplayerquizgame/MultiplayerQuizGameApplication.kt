package com.example.multiplayerquizgame

import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.model.GameEventType
import com.example.multiplayerquizgame.redis.RedisMessagePublisher
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MultiplayerQuizGameApplication

fun main(args: Array<String>) {
	val app = runApplication<MultiplayerQuizGameApplication>(*args)
	//val pub = app.getBean(RedisMessagePublisher::class.java)
}
