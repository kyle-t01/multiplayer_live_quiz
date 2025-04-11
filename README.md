# Multiplayer Live Quiz Demo: A Kotlin + Spring Boot WebSockets Project

This is a real-time multiplayer quiz app built with Kotlin, Spring Boot, and WebSockets. You can compete in a live real-time quiz with other players in a shared lobby.

## Demo Video
[![Demo Video](assets/gameplay.png)](assets/gameplay.mp4)
(if video autoplay does not work, see below .gif)


## Purpose
- explore a bit of Kotlin (coming from Java)
- learn the basics of Spring Boot (inversion of control, dependency injection, beans, annotations)
- play around with WebSockets' real-time persistent connection
- learn how coroutines work

## Core Features
- join lobby with a nickname, and late-joiners to game are auto-kicked
- core game loop (including Quiz Timers) handled by coroutines
- real time updates (through WebSockets): player scores, players joining and leaving the lobby, Quiz Timers
- disconnected players don't disrupt the current game

## Technologies Used
- **Backend**: Kotlin + Spring Boot, WebSockets, Kotlin Coroutines
- **Frontend**: React.js

### Gameplay GIF

![Multiplayer Quiz Demo GIF](assets/gameplay.gif)


## How to run
```
git clone https://github.com/kyle-t01/multiplayer_live_quiz.git
cd multiplayer-live-quiz
```
### Backend (JDK 21+)
```
cd backend
./gradlew bootRun
```
### Frontend (Node.js 22+, npm)
In another terminal (Git Bash):
```
cd frontend
npm install
npm start
```
### Running the App
Backend WebSocket endpoint:
```
ws://localhost:8080/quiz
```
Frontend runs at:
```
http://localhost:3000
```
1. Open the browser http://localhost:3000 (and any additional duplicate tabs to test multiplayer functionality)
2. Enter name and join the lobby (joining while a Game has started will result in a auto-KICK)
3. When enough players in the lobby, start the quiz
4. Questions are timed, and scores are updated real-time



