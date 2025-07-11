# Trivia Night!: A Kotlin + Spring Boot WebSockets Project

This is a real-time multiplayer quiz app built with `Kotlin`, `Spring Boot`, and `WebSockets`.
It is containerised with `Docker`, and deployed via a `Jenkins` CI/CD pipeline to an AWS `EC2` instance.
`Redis` is used for backend pod coordination, and `Kubernetes` (Minikube) for horizontal scaling of WebSocket pods.
You can compete in a live real-time quiz with other players in a shared lobby.

Take a look at the project here:
[Trivia Night!](http://54.79.146.28)

## Purpose
- learn `Kotlin` development (coming from Java)
- learn the basics of `Spring Boot` (inversion of control, dependency injection, beans, annotations)
- implement real-time communications with `WebSockets`
- learn `Kotlin Coroutines` for concurrency management
- learn `Docker & Docker Compose` for containerised deployment
- implement `Jenkins CI/CD` pipelines
- learn `Redis` Pub/Sub and experiment with `Kubernetes` (Minikube) for scalability

## Core Features
- create and join game rooms via codes
- core game loop handled by coroutines
- real time updates through WebSockets: player activity, scores, and timers
- disconnected players do not disrupt the current game
- game rooms are created in least-loaded pods

## Technologies Used
- **Backend**: Kotlin + Spring Boot, WebSockets, Kotlin Coroutines, PostgreSQL
- **Frontend**: React.js
- **Devops CI/CD**: Kubernetes (Minikube), Docker, Docker Compose, AWS EC2, Jenkins
- **Other**: Bash scripting, SSH, SCP, Redis Pub/Sub, NGINX Ingress, pgAdmin

### App Screenshots

Joining a Game Lobby

<img src="assets/join.png" width="30%"/> 

<img src="assets/lobby.png" width="30%"/>

Playing the Game

<img src="assets/gameplay.png" width="60%"/>


## Jenkins CI/CD Pipeline
The role of the Jenkins pipeline is to automate deployment to an AWS EC2 instance, whenever code is pushed GitHub repo main branch.

1. on `git push` to main branch, `Jenkins pipline` is automatically triggered
2. use `scp` to transfer source code and  `docker-compose.yaml` to `EC2` host
3. `ssh` into remote `EC2` instance and build `Docker` images from source code
4. push `Docker` images to `Docker Hub`
5. run `docker-compose down, pull, up -d` on `EC2` instance to redeploy updated containers
6. test whether services are up and running via `docker-compose ps`


## Infrastructure Overview and Interaction
`Kubernetes` was used locally via `Minikube` to experiment via scaling backend Websockets and routing. However, for production deployment on AWS EC2, Docker Compose was used instead due to resource limitations of free tier.

In terms of how `Kubernetes` was used locally:
1. The `frontend React` connects to `Gateway` (/gateway) via Websocket connection (hosted behind `NGINX Ingress`)
2. `Gateway` queries `Redis` to determine which backend pod owns requested room, otherwise selects least-loaded pod
3. `Gateway` responds with a redirect path to frontend (/quiz/<id>)
4. `Frontend` reconnects to new path via `Nginx Ingress`
5. The connected `backend pod`, (deployed as part of a `Kubernetes StatefulSet` with 2 replicas), maintains game state and player activity
6. Any `Redis`, `Websocket`, and game logs are written to the `PostgreSQL database`
7. 7`Redis Pub Sub` is used to propagate whether a `backend pod` is available to `Gateway`


## Live Project link:
[Multiplayer Live Quiz](http://54.79.146.28)

### Running the App

```
1. Open http://54.79.146.28, use multiple tabs for multiplayer testing
2. Enter name and join a game room via a code or create a new room
3. When enough players in the game room, start the quiz
4. Questions are timed, and scores are updated real-time
```

## Further Improvements
### Redis currently is a Single Point of Failure in Pod Communications
- Redis handles backend coordination and communicating which pod owns which game room
- If Redis goes offline, gateway loses track of active backend pods
- A possible improvement would be to explore distributed message brokers like Kafka or RabbitMQ to replace Redis Pub/Sub for fault-tolerant communication.

### Deploy PostgreSQL as Kubernetes StatefulSet
- PostgreSQL current runs as a container within docker compose 
- Can deploy PostgreSQL as a Kubernetes StatefulSet with Persistent Volume Claims to ensure resilience and persistent storage

