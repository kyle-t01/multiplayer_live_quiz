package com.example.multiplayerquizgame.controller

import com.example.multiplayerquizgame.model.*
import com.example.multiplayerquizgame.util.TimerService
import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.springframework.web.socket.WebSocketSession
import kotlin.random.Random

class GameLoopController(private val lobby: Lobby,
                         private val emitter: Emitter
)
{
    // track gameLoop coroutine
    private val game: Game = Game()
    private val gameLoopScope: CoroutineScope = CoroutineScope(CoroutineName("GameLoopScope"))
    private var gameLoopJob: Job? = null
    private val roomCode: String = generateRoomCode()

    companion object {
        // generate ID (not unique)
        fun generateRoomCode(): String{
            val id = Random.nextInt(0,10000).toString().padStart(GAME_ID_LENGTH,'0')
            println("Added game: ${id}")
            return id
        }
        val GAME_ID_LENGTH = 4

    }

    /**
     * Get room code
     *
     * @return
     */
    fun getRoomCode(): String {
        return roomCode
    }

    /**
     * Handle game event
     *
     * @param session
     * @param gameEvent
     */
    fun handleGameEvent(session: WebSocketSession, gameEvent: GameEvent) {
        val type = gameEvent.type
        val data = gameEvent.data


        when(type) {
            GameEventType.JOIN -> handleJoin(session, gameEvent)
            GameEventType.START -> handleStart(session, gameEvent)
            GameEventType.ANSWER -> handleAnswer(session, gameEvent)
            else -> println("unexpected usage of ${gameEvent}!")
        }
    }

    /**
     * Handle join
     *
     * adds player to lobby and game
     *
     * @param session
     * @param gameEvent
     */
    fun handleJoin(session: WebSocketSession, gameEvent: GameEvent) {
        // extract data
        val jsonData = gameEvent.data as JsonNode
        val code = jsonData.get("roomCode")?.asText() ?: ""
        // does room code match?
        if (!code.equals(roomCode)) {
            // no, so don't allow join
            println("roomCode did not match")
            // kick session
            emitter.emit(session, GameEvent(GameEventType.KICK, ""))
            return
        }
        // add player to game lobby
        val name = jsonData.get("playerName")?.asText() ?: "Joining..."
        val player = Player(name, 0, roomCode)
        println("$player")
        lobby.addToPlayers(session, player)
        game.addPlayer(player)

        // still allow players to join when game started
        /*
         if (game.hasStarted() || gameLoopJob?.isActive == true) {
                    // then KICK the player
                    println("Kicking ${player.name} from game.")
                    emit(session, GameEvent(GameEventType.KICK, ""))
                    return
                }
        */

        if (game.hasStarted()) {
            emitStartToSession(session)
        }

        // emit signals
        emitter.emit(session, GameEvent(GameEventType.JOIN, player))
        emitLobbyUpdate()
    }

    /**
     * Handle start
     *
     * @param session
     * @param gameEvent
     */
    fun handleStart(session: WebSocketSession, gameEvent: GameEvent) {
        // if started and not yet ended
        if (game.hasStarted()) {
            println("Game is already in progress...")
            return
        }
        // start the game
        game.start()

        // update the initial score of all players
        emitLobbyUpdate()

        // launch the game loop
        gameLoopJob = gameLoopScope.launch {
            println("Launched Game Loop Coroutine")
            try {
                // tell all players the game has started
                emitStart()
                // start the game loop
                println("game status: isStarted = ${game.hasStarted()}, isEnded =${game.hasEnded()}")
                val timer = TimerService(gameLoopScope)
                while (!game.hasEnded()) {
                    if (hasNoPlayers()) {
                        // no more players, exit
                        return@launch
                    }
                    // send current question
                    emitQuestion()
                    // send total time
                    emitTotalAnswerTime()
                    // start ticking answer timer
                    val answerTimer = timer.startTickingAnswerTimer(
                        onTick = {
                            timeLeft -> emitTime(timeLeft)
                        },
                        task = {
                            println("finished answerTimer...")
                        }
                    )
                    answerTimer.join()



                    emitShow()
                    emitTotalRevealTime()
                    // reveal answers
                    val revealTimer = timer.startTickingRevealTimer(
                        onTick = {
                                timeLeft -> emitTime(timeLeft)
                        },
                        task = {
                            println("finished revealTimer...")
                        }
                    )
                    revealTimer.join()
                    game.prepareNextQuestion()
                }

            } finally {
                // any time coroutine exits
                println("exiting coroutine")
                game.end()
                gameLoopJob = null
                emitEnd()
            }
        }
        return
    }

    fun handleAnswer(session: WebSocketSession, gameEvent: GameEvent) {
        // TODO: multiple player answers
        val ans: Int = gameEvent.data.toString().toInt()
        // validate
        val playerThatAnswered = lobby.getPlayerFromSession(session)
        game.validatePlayerAnswer(playerThatAnswered, ans)
        // give feedback
        emitAnswer(session)
        // broadcast state change to everyone
        emitLobbyUpdate()
        return
    }

    // get current players
    fun getCurrentPlayers(): List<Player> {
        return game.getPlayers()
    }

    // get all sessions connected to this game
    fun getPlayerSessions(): List<WebSocketSession> {
        val players = getCurrentPlayers()
        val sessionList = players.mapNotNull{lobby.getSessionFromPlayer(it)}
        return sessionList
    }

    /**
     * Emit lobby update
     *
     */
    fun emitLobbyUpdate() {
        val event = GameEvent(GameEventType.LOBBY_UPDATE, lobby.getPlayers())
        emitToGameLobby(event)
    }

    /**
     * Emit start
     *
     */
    fun emitStart() {
        val event = GameEvent(GameEventType.START, "")
        emitToGameLobby(event)
    }

    /**
     * Emit start to session
     *
     * Tell player a game has already started when join
     *
     * @param session
     */
    fun emitStartToSession(session: WebSocketSession) {
        val event = GameEvent(GameEventType.START, "")
        emitter.emit(session,event)
    }

    /**
     * Emit question
     *
     */
    fun emitQuestion() {
        val q = game.getCurrentQuestion()
        val event = GameEvent(GameEventType.QUESTION, q)
        emitToGameLobby(event)
    }

    /**
     * Emit total time
     *
     */
    fun emitTotalAnswerTime() {
        val t = TimerService.ANSWER_DURATION
        val event = GameEvent(GameEventType.TOTAL_TIME, t)
        emitToGameLobby(event)
    }

    /**
     * Emit reveal total time
     *
     */
    fun emitTotalRevealTime() {
        val t = TimerService.REVEAL_ANSWER_DURATION
        val event = GameEvent(GameEventType.TOTAL_TIME, t)
        emitToGameLobby(event)
    }

    /**
     * Emit time
     *
     */
    fun emitTime(timeLeft: Long) {
        val event = GameEvent(GameEventType.TIME, timeLeft)
        emitToGameLobby(event)
    }

    /**
     * Emit end
     *
     */
    fun emitEnd() {
        val event = GameEvent(GameEventType.END, "")
        emitToGameLobby(event)
    }

    /**
     * Emit answer
     *
     */
    fun emitAnswer(session: WebSocketSession) {
        //  TODO: should be player's answer
        val data = game.getCurrentAnswer()
        val event = GameEvent(GameEventType.ANSWER, data)
        emitter.emit(session, event)
    }

    /**
     * Emit show
     *
     */
    fun emitShow() {
        val data = game.getCurrentAnswer()
        val event = GameEvent(GameEventType.SHOW, data)
        emitToGameLobby(event)
    }

    /**
     * Emit to all players within game lobby
     *
     * @param event
     */
    private fun emitToGameLobby(event: GameEvent) {
        emitter.emitToAll(getPlayerSessions(), event)
    }

    fun handleDisconnect(session: WebSocketSession) {
        // remove player on disconnect
        val removedPlayer = lobby.removePlayer(session)
        game.removePlayer(removedPlayer)
        emitLobbyUpdate()

        // when all players have disconnected from game, stop game loop
        if (hasNoPlayers()) {
            gameLoopJob?.cancel()
            gameLoopJob = null
        }

    }

    fun hasNoPlayers(): Boolean {
        return game.hasNoPlayers()
    }

}