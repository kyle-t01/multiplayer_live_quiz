package com.example.multiplayerquizgame.redis

import com.example.multiplayerquizgame.model.GameEvent
import com.example.multiplayerquizgame.util.JsonMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Component

@Component
class RedisMessagePublisher(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val topic: ChannelTopic,
    private val mapper: JsonMapper
) {
    fun publish(event: GameEvent) {
        val data = mapper.convertToTextMessage(event)
        println("attempting to publish $data to $topic")
        println("should call convert and send")
        redisTemplate.convertAndSend(topic.topic, data)
    }
}