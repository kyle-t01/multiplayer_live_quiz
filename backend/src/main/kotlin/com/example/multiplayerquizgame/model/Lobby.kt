package com.example.multiplayerquizgame.model

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class Lobby(
    val players: MutableMap<WebSocketSession, Player> = mutableMapOf(),
    val quiz: Quiz = Quiz(),
    var isGameStarted: Boolean = false,

) {

    // remove a <session, player> from lobby on disconnect
    fun removePlayer(session: WebSocketSession) {
        val player= players[session]
        println("$player LEFT the game! [${players.size} players left...]")
        players.remove(session)

        // if there are no more players, then the game has ended
        if (players.isEmpty()) {
            isGameStarted = false;
        }
        return
    }

    // return a List<Player> currently in the lobby
    fun getPlayers():List<Player> {
        return players.values.toList()
    }

    // add a <session, player> pair to players
    fun addToPlayers(session: WebSocketSession, player: Player) {
        // associate the session with this player
        players[session] = player
        return
    }

    // start the game
    fun startGame() {
        println("Game has officially started.")
        isGameStarted = true
        // load Quiz
        quiz.loadQuiz()
        // reset the scores of every player
        for (p in getPlayers()) {
            p.resetScore()
        }
    }

    // end the game
    fun endGame() {
        println("Game has been terminated.")
        isGameStarted = false
        // reset Quiz
        quiz.endQuiz()
    }

    // validate a player's answer
    fun validateAnswer(session: WebSocketSession, ans: Int) {
        val player = players[session]

        // change player score based on answer
        if (quiz.getCurrentA().contains(ans)) {
            println("$player has answered $ans [V] correct")
            player!!.qCorrect += 1
        } else {
            println("$player has answered $ans [X] incorrect")
        }

    }

}