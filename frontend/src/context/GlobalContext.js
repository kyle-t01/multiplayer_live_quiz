// store important global variables

import { useContext, createContext, useState, useRef } from "react";

const GlobalContext = createContext();

export const GlobalContextProvider = ({ children }) => {

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
        const gameEvent = {
            type: type.toUpperCase(),
            data: data,
        };
        socketRef.current.send(JSON.stringify(gameEvent));
        console.log(`sent a game event: ${type}: ${data}`);
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

        // attempt connection
        if (!socketRef.current || socketRef.current.readyState === WebSocket.CLOSED) {
            socketRef.current = new WebSocket(`ws://${window.location.hostname}:8080/quiz`);
        }
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

    // when the player joins the lobby, open connection to websocket
    const handlePlayerJoin = () => {
        if (!playerName.trim() || !roomCode.trim() || roomCode.length != 4) return;

        // attempt connection
        if (!socketRef.current || socketRef.current.readyState === WebSocket.CLOSED) {
            socketRef.current = new WebSocket(`ws://${window.location.hostname}:8080/quiz`);
        }
        // establish connection
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
    };

    // messages sent from the game server
    const handleGameEventMessage = (message) => {
        const gameEvent = JSON.parse(message.data);
        console.log("server sent GameEvent:");
        console.log(gameEvent);
        switch (gameEvent.type) {
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