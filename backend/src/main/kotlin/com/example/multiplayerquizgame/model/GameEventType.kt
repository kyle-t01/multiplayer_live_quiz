package com.example.multiplayerquizgame.model

enum class GameEventType {
    JOIN, // player attempted to join a game
    LOBBY_UPDATE, // update lobby information
    START, // game has started
    END, // game has ended
    ANSWER, // player has answered
    QUESTION, // send question to players
    SHOW, // reveal answers
    KICK, // kick player out of lobby
    LEAVE, // player left
    TIME, // time remaining for a timer
    TOTAL_TIME, // total time allocated for a timer
}