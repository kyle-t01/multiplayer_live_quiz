package com.example.multiplayerquizgame.model

class Game()
{
    private var players: MutableList<Player> = mutableListOf()
    private var isStarted:Boolean = false
    private var isEnded: Boolean = false
    private var quiz: Quiz = Quiz.createQuizDefault()

    fun hasStarted(): Boolean {
        return isStarted
    }

    fun hasEnded(): Boolean {
        return isEnded
    }

    // fun get current state

    // starting and stopping games
    fun start() {
        println("GAME HAS STARTED")
        isStarted = true
        isEnded = false
        // load quiz (assume loaded by default)

        // reset the scores of every player
        for (p in players) {
            p.resetScore()
        }

    }

    fun end() {
        println("GAME HAS ENDED")
        isStarted = false
        isEnded = true
        // reset quiz
        // don't clear questions, just reset it to start
        quiz.endQuiz()
    }

    // validate player answer
    fun validatePlayerAnswer(player: Player?, ans: Int): Boolean {
        if (player == null) return false

        // change player score immediately based on answer
        if (quiz.getCurrentA().contains(ans)) {
            println("$player has answered $ans [V] correct")
            player.qCorrect += 1
            return true
        } else {
            println("$player has answered $ans [X] incorrect")
            return false
        }

    }


    // fun printGameState()

    /**
     * Add player to a Game
     *
     * @param player
     * @return added player to game, and whether can be added
     */
    fun addPlayer(player: Player?): Boolean {
        if (player == null) return false

        // attempted to add a duplicate
        if (players.contains(player)) return false
        players.add(player)

        // did we exceed player capacity?
        // TODO: implement
        return true
    }

    fun removePlayer(player: Player?) {
        // remove corresponding player
        players.remove(player)

        // if there are no more players, end the game
        if (hasNoPlayers()) {
            end()
        }
        return
    }

    fun getPlayers(): MutableList<Player> {
        return players
    }

    fun hasNoPlayers(): Boolean {
        return players.isEmpty()
    }

    fun getCurrentQuestion(): Question {
        // assume that game has started
        // get question
        return quiz.getCurrentQ()
    }

    fun getCurrentAnswer(): List<Int> {
        return quiz.getCurrentA()
    }

    fun prepareNextQuestion(): Boolean {
        // prepare the next question, return f when no Qs left
        // if cannot prepare next question, then end the game
        if (!quiz.prepareNextQ()) {
            isEnded = true
        }
        return quiz.prepareNextQ()
    }

}
