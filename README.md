# Multiplayer Live Quiz Demo: A Kotlin + Spring Boot WebSockets Project

This is a real-time multiplayer quiz app built with **Kotlin**, **Spring Boot**, and **WebSockets**.
It is containerised with **Docker** and **Docker Compose**, and deployed via a Jenkins CI/CD pipeline to an AWS EC2 instance.
You can compete in a live real-time quiz with other players in a shared lobby.

Take a look at the project here:
[Multiplayer Live Quiz](http://54.79.146.28)

## Demo Video
[![Demo Video](assets/gameplay.png)](assets/gameplay.mp4)
(if video autoplay does not work, see below .gif)


## Purpose
- learn Kotlin development (coming from Java)
- learn the basics of Spring Boot (inversion of control, dependency injection, beans, annotations)
- implement WebSockets' real-time persistent connection
- learn how coroutines work
- learn about Docker containerisation (Dockerfiles, Docker compose) to improve production workflow
- implement Jenkins CI/CD pipelines and deploy to AWS EC2

## Core Features
- join lobby with a nickname, and late-joiners to game are auto-kicked
- core game loop (including Quiz Timers) handled by coroutines
- real time updates (through WebSockets): player scores, players joining and leaving the lobby, Quiz Timers
- disconnected players don't disrupt the current game

## Technologies Used
- **Backend**: Kotlin + Spring Boot, WebSockets, Kotlin Coroutines
- **Frontend**: React.js
- **Devops CI/CD**: Docker, Docker Compose, AWS EC2, Jenkins
- **Other**: bash scripting, ssh, scp

### Gameplay GIF

![Multiplayer Quiz Demo GIF](assets/gameplay.gif)

## Jenkins CI/CD Pipeline
The role of the Jenkins pipeline is to automate deployment to an AWS EC2 instance, whenever code is pushed GitHub repo main branch.

1. on `git push` to main branch, `Jenkins pipline` is automatically triggered
2. use `scp` to transfer source code and  `docker-compose.yaml` to `EC2` host
3. `ssh` into remote `EC2` instance and build `Docker` images from source code
4. push `Docker` images to `Docker Hub`
5. run `docker-compose down, pull, up -d` on `EC2` instance to redeploy updated containers
6. test whether services are up and running via `docker-compose ps`


## Live Project link:
[Multiplayer Live Quiz](http://54.79.146.28)

### Running the App

```
1. Open http://54.79.146.28, use multiple tabs for multiplayer testing
2. Enter name and join the lobby (joining while a Game has started will result in an auto-KICK)
3. When enough players in the lobby, start the quiz
4. Questions are timed, and scores are updated real-time
```

## Addendum
After completing this project, I felt the urge to create something more complex and "interactive" than just a simple quiz game.
So I've started working on my next real-time multiplayer project: Chaos Chess Online, where you could mix and match pieces from different chess games (Xiangqi, Checkers, Chess)!
You will be able to play against other players, or with a mini-maxing AI!

Have a look at it (work in progress) here:
[Chaos Chess Online](https://github.com/kyle-t01/chaos_chess_online)

Also, as the next step in learning more about devops practices and tools: explore Kubernetes and Terraform