package com.example.multiplayerquizgame.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
@Service
class RedisGameRoomRegistry (
    private val redisTemplate: RedisTemplate<String, Any>?
){
    // put <GameRoom, HostName>
    /**
     * Register Game Room
     *
     * @param roomCode
     * @param hostStr
     */
    fun registerGameRoom(roomCode: String, hostStr: String) {
        if (!hasRedisConnection()) return
        insert(roomCode, hostStr)
        println("Room $roomCode registered to $hostStr")

    }

    /**
     * Get game room host
     *
     * @param roomCode
     * @return
     */
    fun getGameRoomHost(roomCode: String): String? {
        if (!hasRedisConnection()) return null
        val host = get(roomCode)
        return host
    }

    /**
     * Unregister Game Room
     *
     * @param roomCode
     */
    fun unregisterGameRoom(roomCode: String) {
        if (!hasRedisConnection()) return
        redisTemplate!!.delete("$KEY_PREFIX:$roomCode")
        println("Room $roomCode unregistered")
    }

    /**
     * Unregister all rooms of pod
     *
     * @param podName
     */
    fun unregisterAllRoomsOfPod(podName: String) {
        if (!hasRedisConnection()) return
        val keys = getAllGameRoomKeys() ?: return
        for (k in keys) {
            val value = get(k)
            if (value == podName) {
                // pod owns this value
                redisTemplate!!.delete(k)
                println("deleted key $k for pod $podName")
            }
        }
    }

    private fun getAllGameRoomKeys(): Set<String>? {
        if (!hasRedisConnection()) return null
        val keys = redisTemplate!!.keys("$KEY_PREFIX:*") ?: return null
        return keys
    }

    /**
     * Has redis connection
     *
     * @return
     */
    fun hasRedisConnection(): Boolean {
        if (redisTemplate == null) {
            println("RedisGameRoomRegistry: No connection to Redis!")
            return false
        }
        return true
    }

    /**
     * Insert <K,V> = <game-room:<roomCode>, <value>>
     *
     * @param roomCode
     * @param value
     */
    private fun insert(roomCode: String, value: String) {
        if (redisTemplate == null) return
        redisTemplate.opsForValue().set("$KEY_PREFIX:$roomCode", value)
        // TODO: need a logger class
        return
    }

    /**
     * Get owner (host) of given roomCode
     *
     * @param roomCode
     * @return
     */
    private fun get(roomCode: String): String? {
        if (redisTemplate == null) return null
        val result = redisTemplate.opsForValue().get("$KEY_PREFIX:$roomCode") as? String
        return result
    }

    companion object {
        val KEY_PREFIX = "game-room"
    }
}