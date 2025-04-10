import { GlobalVars } from "../context/GlobalContext";


const InputBox = () => {

    // global state
    const { playerName, setPlayerName, handlePlayerJoin, hasJoined } = GlobalVars();

    const maxChars = 8;


    return (
        <div className="input-box" hidden={hasJoined}>
            <h2>Join Quiz Lobby Here</h2>
            <input
                className="input"
                type="text"
                placeholder="Username..."
                value={playerName}
                onChange={(e) => { setPlayerName(e.target.value) }}
                rows={1}
                maxLength={maxChars}
            />
            <div className="char-counter">
                {playerName.length} / {maxChars}
            </div>
            <button className="button" onClick={handlePlayerJoin}>
                Join Lobby
            </button>
        </div>
    );

}

export default InputBox;