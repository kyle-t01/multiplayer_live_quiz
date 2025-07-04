package com.example.multiplayerquizgame.log
import org.springframework.stereotype.Component

// for now, Logger will be used as a singleton class accessible by everyone
@Component
class Logger (
    private val repo: LogEntryRepo
){
    // There are three types of logs
    // (1) Websocket logs - websocket GameEvents sent to backend gameLoop by player
    // (2) Backend Pod logs - redis, pod init logs, inter-pod communications
    // (3) GameLoop logs - logs created during game loop, game creation and deletion
    // simple format: [severity] [podName] [roomCode] [timeStamp] [details]
    private var podName:String? = getPodName()


    fun logWS(roomCode: String?, details: String?) {
        formattedLog("ws", podName, roomCode, getCurrentTime(), details)
    }

    fun logGL(roomCode: String?, details: String?) {
        formattedLog("gl", podName, roomCode, getCurrentTime(), details)
    }

    fun logPod(roomCode: String?, details: String?) {
        formattedLog("pod", podName, roomCode, getCurrentTime(), details)
    }

    fun getPodName(): String {
        if (podName == null) {
            podName = System.getenv("HOSTNAME") ?: ""
        }
        return podName!!
    }

    private fun getCurrentTime(): String {
        return java.time.LocalDateTime.now().toString()
    }

    private fun formattedLog(category: String, podName: String?, roomCode: String?, timestamp: String, details: String?) {
        println("$category, $podName, $roomCode, $timestamp, $details")
        // save to database
        repo.save(
            LogEntry(
            category = category,
            podName = podName,
            roomCode = roomCode,
            timeStamp = timestamp,
            details = details
            )
        )
    }



}