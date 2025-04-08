// store important global variables

import { useContext, createContext, useState, useRef } from "react";

const GlobalContext = createContext();

export const GlobalContextProvider = ({ children }) => {

    const socketRef = useRef(null);
    const [playerName, setPlayerName] = useState("");
    const [hasJoined, setHasJoined] = useState(false);
    const [lobby, setLobby] = useState([]);
    const [hasGameStarted, setHasGameStarted] = useState(false);
    const [question, setQuestion] = useState(null);
    const [userAnswer, setUserAnswer] = useState([]);

    return (
        <GlobalContext.Provider
            value={{
                socketRef,
                playerName, setPlayerName,
                hasJoined, setHasJoined,
                lobby, setLobby,
                hasGameStarted, setHasGameStarted,
                question, setQuestion,
                userAnswer, setUserAnswer,
            }}>
            {children}
        </GlobalContext.Provider>
    );
}

// export the context 
export const GlobalVars = () => {
    return useContext(GlobalContext)
}