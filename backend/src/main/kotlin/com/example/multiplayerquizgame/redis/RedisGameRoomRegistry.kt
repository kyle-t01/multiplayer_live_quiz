package com.example.multiplayerquizgame.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
@Service
class RedisGameRoomRegistry (
    private val redisTemplate: RedisTemplate<String, Any>?
){

    /**
     * Add game room to pod
     *
     * ie: pod-rooms:XXXX -> Set<YYYY,ZZZZ>
     *
     * @param roomCode
     * @param hostStr
     */
    fun addRoomToPod(roomCode: String, podName: String) {
        if (!hasRedisConnection()) return
        println("$podName owns $roomCode")
        sadd(podName, roomCode)
    }

    /**
     * Remove room from pod
     *
     * @param roomCode
     * @param podName
     */
    fun removeRoomFromPod(roomCode: String, podName: String) {
        if (!hasRedisConnection()) return
        println("$podName no longer owns $roomCode")
        srem(podName, roomCode)
    }

    /**
     * Unregister all rooms of pod
     *
     * @param podName
     */
    fun removeAllRoomsOfPod(podName: String) {
        if (!hasRedisConnection()) return
        val podSet = smembers(podName)
        println("removing all rooms of pod $podName")
        for (member in podSet) {
            srem(podName, member)
        }
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
     * Add to set
     *
     * @param set
     * @param member
     */
    private fun sadd(set: String, member: String) {
        if (redisTemplate == null) return
        redisTemplate.opsForSet().add("$SET_PREFIX:$set", member)
    }

    /**
     * Remove from set
     *
     * @param set
     * @param member
     */
    private fun srem(set: String, member: String) {
        if (redisTemplate == null) return
        redisTemplate.opsForSet().remove("$SET_PREFIX:$set", member)
    }

    /**
     * Set membership
     *
     * @param set
     * @param member
     * @return
     */
    private fun sismember(set: String, member: String): Boolean {
        if (redisTemplate == null) return false
        val membership = redisTemplate.opsForSet().isMember("$SET_PREFIX:$set", member)
        return (membership == true)
    }

    /**
     * Size of set
     *
     * @param set
     * @return
     */
    private fun scard(set: String): Long {
        if (redisTemplate == null) return 0L
        val size = redisTemplate.opsForSet().size("$SET_PREFIX:$set") ?: 0L
        return size
    }

    /**
     * Members of a set
     *
     * @param set
     * @return
     */
    private fun smembers(set: String): Set<String> {
        if (redisTemplate == null) return emptySet()
        val members = redisTemplate.opsForSet().members("$SET_PREFIX:$set")
        val result = mutableSetOf<String>()
        members?.forEach { it ->
            if (it is String) {
                result.add(it)
            }
        }
        return result
    }


    companion object {
        val KEY_PREFIX = "game-room"
        val SET_PREFIX = "pod-rooms"
    }
}

/**
 * From Redis Docs
 * Basic commands
 * SADD adds a new member to a set.
 * SREM removes the specified member from the set.
 * SISMEMBER tests a string for set membership.
 * SINTER returns the set of members that two or more sets have in common (i.e., the intersection).
 * SCARD returns the size (a.k.a. cardinality) of a set.
 */