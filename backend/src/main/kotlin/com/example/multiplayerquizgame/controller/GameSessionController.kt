package com.example.multiplayerquizgame.controller

import com.example.multiplayerquizgame.model.*
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class GameSessionController (private val lobby: Lobby, private val emitter: Emitter) {

    // goal of class is to
    // create and delete new games
    // handle any signals to games
    // manage a list of games
    val games: MutableList<GameLoopController> = mutableListOf()

    fun createGame(): GameLoopController? {
        if (games.size >= MAX_GAMES) {
            // EXCEED max games
            println("Too many games, currently ${games.size}")
            return null
        }
        // else, add the game
        val game = GameLoopController(lobby, emitter)
        games.add(game)
        println("game ${game.getRoomCode()} created")
        return game
    }

    private fun findGameRoomFromSession(session: WebSocketSession): GameLoopController? {
        val roomCode = lobby.getRoomCodeFromSession(session) ?: return null
        return findGameRoomFromRoomCode(roomCode)
    }

    private fun findGameRoomFromRoomCode(roomCode: String?): GameLoopController? {
        if (roomCode == null || roomCode == "") return null
        return games.find {it.getRoomCode() == roomCode}
    }

    fun handleInternalGameEventTraffic(session: WebSocketSession, gameEvent: GameEvent) {
        // when creating or joining a game, not possible to associate session with game
        val type = gameEvent.type
        val jsonData = gameEvent.data as JsonNode
        var game: GameLoopController? = null
        var player: Player? = lobby.getPlayerFromSession(session)
        when(type) {
            GameEventType.CREATE -> {
                // create game
                game = createGame()
                if (game == null) return
                // create player
                player = newPlayerFromGameEvent(gameEvent)
                lobby.addToPlayers(session, player)
                println("creating... $player")
            }
            GameEventType.JOIN -> {
                // find game by roomCode
                val roomCode = jsonData.get("roomCode")?.asText()
                game = findGameRoomFromRoomCode(roomCode)
                // can't find game, then KICK
                if (game == null) {
                    println("Room code $roomCode did not match any games!")
                    emitter.emit(session, GameEvent(GameEventType.KICK, ""))
                    return
                }
                player = newPlayerFromGameEvent(gameEvent)
                player.roomCode = game.getRoomCode()
                lobby.addToPlayers(session, player)
            }
            else -> {
                // find game by session
                game = findGameRoomFromSession(session)
            }
        }
        if (game == null || player == null) return
        // handle game event
        game.handleGameEvent(player, gameEvent)
        return
    }

    fun handleExternalGameEventTraffic(topic: String, gameEvent: GameEvent) {
        val topicParts = getTopicParts(topic)
        val prefix = topicParts[0]
        val id = topicParts[1]
        when(prefix) {
            "game-room" -> handleRedisRoomEvent(id, gameEvent)
            "player-id" -> handleRedisPlayerEvent(id, gameEvent) // emit directly to player
            else -> println("unknown topic prefix: $prefix")
        }
    }


    // merge together
    // fun handleGameEventTraffic
    fun handleConnectionClosed(session: WebSocketSession) {
        // find game associated with session
        val game = findGameRoomFromSession(session)
        // is associated with a game?
        if (game == null) {
            // no, so do nothing
            return
        }
        // yes, then delegate disconnect to game loop
        game.handleDisconnect(session)
        // if there are no players in the game, can safely remove it
        if (game.hasNoPlayers()) {
            // no players, game needs to be removed
            println("game with ${game.getRoomCode()} removed due to empty lobby")
            games.remove(game)
        }
    }
    // handle external game events


    // from a player to a (external) room
    fun handleRedisRoomEvent(id: String, gameEvent: GameEvent) {
        println("got room event [$id]: $gameEvent")
        // find the room where event is being sent to
        val game = findGameRoomFromRoomCode(id)

    }

    // from (external) room to a player
    fun handleRedisPlayerEvent(id: String, gameEvent: GameEvent) {
        println("got player event [$id]: $gameEvent")
    }

    // get the components of topic
    private fun getTopicParts(topic: String): List<String> {
        val parts = topic.split(":")
        if (parts.size != 2) {
            println("topic format invalid $topic")
            return listOf("","")
        }
        return parts
    }

    /**
     * New player from game event
     *
     * @param gameEvent
     * @return
     */
    private fun newPlayerFromGameEvent(gameEvent: GameEvent): Player {
        // TODO: refactor this with mapper class
        val jsonData = gameEvent.data as JsonNode
        val name = jsonData.get("playerName")?.asText() ?: "Joining..."
        val code = jsonData.get("roomCode")?.asText() ?: ""
        return Player(name, 0, code)
    }

    companion object {
        val MAX_GAMES = 3
    }



}