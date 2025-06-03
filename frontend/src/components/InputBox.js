import { GlobalVars } from "../context/GlobalContext";


const InputBox = () => {

    // global state
    const { playerName, setPlayerName, roomCode, setRoomCode, handlePlayerJoin, hasJoined } = GlobalVars();

    const maxChars = 10;
    const maxCodesize = 4;

    return (
        <div className="input-box" hidden={hasJoined}>
            <h2>Join Quiz Lobby Here</h2>
            <input
                className="input"
                type="text"
                placeholder="USERNAME..."
                value={playerName}
                onChange={(e) => { setPlayerName(e.target.value) }}
                rows={1}
                maxLength={maxChars}
            />
            <div className="char-counter">
                {playerName.length} / {maxChars}
            </div>
            <input
                className="input"
                type="text"
                placeholder="ROOM CODE..."
                value={roomCode}
                onChange={(e) => { setRoomCode(e.target.value) }}
                rows={1}
                maxLength={maxCodesize}
            />
            <button className="button" onClick={handlePlayerJoin}>
                Connect to Room
            </button>
        </div>
    );

}

export default InputBox;