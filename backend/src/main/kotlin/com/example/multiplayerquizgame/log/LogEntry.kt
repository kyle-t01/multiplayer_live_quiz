package com.example.multiplayerquizgame.log
import jakarta.persistence.*
import org.springframework.data.jpa.repository.JpaRepository

interface LogEntryRepo: JpaRepository<LogEntry, Int>

@Entity
@Table(name = "logs")
data class LogEntry(
    // simple format: [severity] [podName] [roomCode] [timeStamp] [details]
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Int = 0,
    val category: String,
    val podName:String?,
    val roomCode:String?,
    val timeStamp:String,
    val details:String?
) {

}

