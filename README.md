# Multiplayer Live Quiz Demo: A Kotlin + Spring Boot WebSockets Project

This is a real-time multiplayer quiz app built with **Kotlin**, **Spring Boot**, and **WebSockets**.
It is containerised with **Docker** and **Docker Compose**.
You can compete in a live real-time quiz with other players in a shared lobby.

## Demo Video
[![Demo Video](assets/gameplay.png)](assets/gameplay.mp4)
(if video autoplay does not work, see below .gif)


## Purpose
- learn Kotlin development (coming from Java)
- learn the basics of Spring Boot (inversion of control, dependency injection, beans, annotations)
- implement WebSockets' real-time persistent connection
- learn how coroutines work
- learn about Docker containerisation (Dockerfiles, Docker compose) to improve production workflow

## Core Features
- join lobby with a nickname, and late-joiners to game are auto-kicked
- core game loop (including Quiz Timers) handled by coroutines
- real time updates (through WebSockets): player scores, players joining and leaving the lobby, Quiz Timers
- disconnected players don't disrupt the current game

## Technologies Used
- **Backend**: Kotlin + Spring Boot, WebSockets, Kotlin Coroutines
- **Frontend**: React.js
- **Other**: Docker

### Gameplay GIF

![Multiplayer Quiz Demo GIF](assets/gameplay.gif)


## How to run
1. First clone the repo

```
git clone https://github.com/kyle-t01/multiplayer_live_quiz.git
cd multiplayer-live-quiz
```
Then, if you have Docker, jump to step 2

2. Ensure you already have [Docker Desktop](https://www.docker.com/products/docker-desktop/) installed and run:

```
docker-compose up
```

### Running the App

```
1. Open the browser and go to the the frontend http://localhost:3000 
2. To test multiplayer functionality, just open http://localhost:3000 in multiple tabs
3. Enter name and join the lobby (joining while a Game has started will result in an auto-KICK)
4. When enough players in the lobby, start the quiz
5. Questions are timed, and scores are updated real-time
6. When you are finished, run: docker-compose down
```


## Addendum
After completing this project, I felt the urge to create something more complex and "interactive" than just a simple quiz game.
So I've started working on my next real-time multiplayer project: Chaos Chess Online, where you could mix and match pieces from different chess games (Xiangqi, Checkers, Chess)!
You will be able to play against other players, or with a mini-maxing AI!

Have a look at it (work in progress) here:
[Chaos Chess Online](https://github.com/kyle-t01/chaos_chess_online)

Also, as the next step in learning more about devops practices and tools: explore Kubernetes and Terraform