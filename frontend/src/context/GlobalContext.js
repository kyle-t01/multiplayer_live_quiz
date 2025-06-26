// store important global variables

import { useContext, createContext, useState, useRef } from "react";

const GlobalContext = createContext();

export const GlobalContextProvider = ({ children }) => {
    const ec2env = true
    const isDev = (window.location.port === "3000") || (ec2env == true);
    const local = isDev
        ? `ws://${window.location.hostname}:8080/quiz`
        : `ws://${window.location.host}/quiz`;
    const gateway = isDev
        ? `ws://${window.location.hostname}:8080/quiz`
        : `ws://${window.location.hostname}:8080/gateway`

    const socketRef = useRef(null);
    const [playerName, setPlayerName] = useState("");
    const [roomCode, setRoomCode] = useState("")
    const [hasJoined, setHasJoined] = useState(false);
    const [lobby, setLobby] = useState([]);
    const [hasGameStarted, setHasGameStarted] = useState(false);
    const [hasGameEnded, setHasGameEnded] = useState(false);
    const [question, setQuestion] = useState(null);
    const [userAnswer, setUserAnswer] = useState(null);
    const [isShowAnswer, setIsShowAnswer] = useState(false);
    const [timeLeft, setTimeLeft] = useState(0);
    const [totalTime, setTotalTime] = useState(0);
    // ie: sendEvent(STRING, Object)
    const sendGameEvent = (type, data) => {
        const gameEvent = makeGameEvent(type, data);
        socketRef.current.send(JSON.stringify(gameEvent));
        console.log(`sent a game event: ${type}: ${data}`);
    }


    const makeGameEvent = (type, data) => {
        const gameEvent = {
            type: type.toUpperCase(),
            data: data,
        };
        return gameEvent
    }

    const handleStartGame = () => {
        sendGameEvent("START", "");
    }
    const handleUserAnswer = (i) => {
        if (userAnswer != null || isShowAnswer) {
            // user already answered, or answer already revealed
            return;
        }
        setUserAnswer(i);
        console.log("You selected: ", i);
        sendGameEvent("ANSWER", i);
    }

    // handle create game, using similar logic to handlePlayerJoin for now
    const handleCreateGame = () => {
        if (!playerName.trim()) return;
        // attempt connection to gateway
        const type = "CREATE"
        const data = { playerName: playerName, roomCode: "" }
        const gameEvent = makeGameEvent(type, data)
        if (isDev) {
            handleCreateRedirect("")
        } else {
            connectToGateWay(gateway, gameEvent)
        }
    }

    // when the player joins the lobby, open connection to websocket
    const handlePlayerJoin = () => {
        if (!playerName.trim() || !roomCode.trim() || roomCode.length != 4) return;
        // attempt connection to gateway
        const type = "JOIN"
        const data = { playerName: playerName, roomCode: roomCode }
        const gameEvent = makeGameEvent(type, data)
        if (isDev) {
            handleJoinRedirect("")
        } else {
            connectToGateWay(gateway, gameEvent)
        }
    };

    const handleCreateRedirect = (wsLink) => {
        // est connection
        const ws = (ec2env) ? local : `${local}/${wsLink}`;
        socketRef.current = new WebSocket(ws);
        console.log(`${local}/${wsLink}`)
        // TODO: close the original gateway ws connection
        // establish connection
        socketRef.current.onopen = () => {
            console.log('websocket open!');
            console.log(`${playerName} attempting to create a new Room`)
            const data = { playerName: playerName, roomCode: "" }
            sendGameEvent('create', data);
        };
        // receive message
        socketRef.current.onmessage = (message) => {
            handleGameEventMessage(message);
        };
        // error
        socketRef.current.onerror = (e) => {
            console.log('websocket error!', e);
        };
        // disconnect
        socketRef.current.onclose = () => {
            console.log('websocket disconnected!');
        };
    }


    // join redirect
    const handleJoinRedirect = (wsLink) => {
        // establish connection
        const ws = (ec2env) ? local : `${local}/${wsLink}`;
        socketRef.current = new WebSocket(ws);
        console.log(`${local}/${wsLink}`)
        // TODO: close the original gateway ws connection
        socketRef.current.onopen = () => {
            console.log('websocket open!');
            console.log(`${playerName} attempting to join lobby with code ${roomCode}`)
            const joinData = { playerName: playerName, roomCode: roomCode }
            sendGameEvent('join', joinData);
        };
        // receive message
        socketRef.current.onmessage = (message) => {
            handleGameEventMessage(message);
        };
        // error
        socketRef.current.onerror = (e) => {
            console.log('websocket error!', e);
        };
        // disconnect
        socketRef.current.onclose = () => {
            console.log('websocket disconnected!');
        };
    }


    // connect to gateway
    const connectToGateWay = (wsLink, gameEvent) => {
        const ws = new WebSocket(wsLink);
        // on connection
        ws.onopen = () => {
            console.log(`websocket to ${wsLink} open!`);
            // send event
            ws.send(JSON.stringify(gameEvent))
            console.log("sent a game event to gateway!")
        }
        // receive message
        ws.onmessage = (message) => {
            handleGameEventMessage(message);
        };
        // error
        ws.onerror = (e) => {
            console.log('websocket error!', e);
        };
        // disconnect
        ws.onclose = () => {
            console.log('websocket disconnected!');
        };
    }

    // messages sent from the game server
    const handleGameEventMessage = (message) => {
        const gameEvent = JSON.parse(message.data);
        console.log("server sent GameEvent:");
        console.log(gameEvent);
        switch (gameEvent.type) {
            case "REDIRECT":
                console.log("ERROR, incorrect redirect type!")
                break;
            case "REDIRECT_CREATE":
                const backendCreateName = gameEvent.data
                console.log("redirect to backend (create): ", backendCreateName)
                handleCreateRedirect(backendCreateName)
                break;
            case "REDIRECT_JOIN":
                const backendJoinName = gameEvent.data
                console.log("redirect to backend (join): ", backendJoinName)
                handleJoinRedirect(backendJoinName)
                break;
            case "TIME":
                const time = gameEvent.data
                setTimeLeft(time);
                break;
            case "TOTAL_TIME":
                setTotalTime(gameEvent.data);
                break;
            case "JOIN":
                setHasJoined(true);
                console.log("You have joined the lobby!");
                break;
            case "LOBBY_UPDATE":
                setLobby(gameEvent.data);
                console.log("Updating current lobby!");
                break;
            case "START":
                setHasGameStarted(true);
                setHasGameEnded(false);
                console.log("Starting or Joining an existing game!");
                break;
            case "END":
                setHasGameStarted(false);
                setHasGameEnded(true);
                setUserAnswer(null);
                setIsShowAnswer(false)
                setQuestion(null)
                console.log("Game has ended!");
                break;
            case "QUESTION":
                setIsShowAnswer(false);
                setQuestion(gameEvent.data);
                setUserAnswer(null);
                console.log("GOT A QUESTION");
                break;
            case "SHOW":
                setIsShowAnswer(true);
                console.log("SHOWING CURRENT ANSWERS");
                break;
            case "KICK":
                setHasGameStarted(false);
                setHasJoined(false)
                // check if player has been KICKED
                console.log("You were KICKED from the game!");
                socketRef.current.close();
                const kickMessage = gameEvent.data
                alert(kickMessage)
                break;
            case "ANSWER":
                console.log("Your answer was received!");
                break;
            default:
                alert("UNKNOWN IMPLEMENTATION of", gameEvent);
        }
    }


    return (
        <GlobalContext.Provider
            value={{
                socketRef,
                playerName, setPlayerName,
                roomCode, setRoomCode,
                hasJoined, setHasJoined,
                lobby, setLobby,
                hasGameStarted, setHasGameStarted,
                question, setQuestion,
                userAnswer, setUserAnswer,
                isShowAnswer, setIsShowAnswer,
                timeLeft, totalTime,
                sendGameEvent,
                handleStartGame,
                handlePlayerJoin,
                handleCreateGame,
                handleUserAnswer,
            }}>
            {children}
        </GlobalContext.Provider>
    );
}

// export the context 
export const GlobalVars = () => {
    return useContext(GlobalContext)
}