import { GlobalVars } from "../context/GlobalContext";


const Lobby = () => {

    // global state
    const { playerName, handleStartGame, hasGameStarted, hasJoined, lobby } = GlobalVars();

    if (!hasJoined) return;
    const renderPlayerCard = (p, i) => {
        if (playerName == p.name) {
            // render the current player's player-card
            return (
                <div className='self-player-card' key={i}>
                    <p>{p.name}</p>
                    <p>{p.qcorrect}</p>
                </div>
            );
        }
        return (
            <div className='player-card' key={i}>
                <p>{p.name}</p>
                <p>{p.qcorrect}</p>
            </div>
        );
    }


    return (
        <div>
            <h2>Active Players</h2>
            <div className="lobby">
                {lobby.map((p, i) => renderPlayerCard(p, i))}
            </div>

            <button className="start-button" onClick={handleStartGame} hidden={hasGameStarted}>
                Start Quiz
            </button>
        </div>
    );
}



export default Lobby;