import { GlobalVars } from "../context/GlobalContext";


const Lobby = () => {
    // global state
    const { playerName, handleStartGame, hasGameStarted, hasJoined, lobby } = GlobalVars();
    if (!hasJoined) return;

    const roomCode = lobby[0]?.roomCode
    const sortedPlayers = [...lobby].sort((a, b) => b.qcorrect - a.qcorrect);
    const maxScore = sortedPlayers[0]?.qcorrect ?? 0;

    const renderPlayerCard = (p, i) => {
        const isSelf = playerName === p.name
        const isFirst = (p.qcorrect === maxScore) && (maxScore > 0)
        const baseCardClass = isSelf ? 'self-player-card' : 'player-card'
        const goldBorderClass = isFirst ? 'gold' : ''

        return (
            <div className={`${baseCardClass} ${goldBorderClass}`} key={i}>
                <p>{p.name}</p>
                <p>{p.qcorrect}</p>
            </div>
        );
    }


    return (
        <div>
            <p className="room-code">Room {roomCode}</p>
            <h3>Active Players</h3>
            <div className="lobby">
                {sortedPlayers.map((p, i) => renderPlayerCard(p, i))}
            </div>
            <button className="start-button" onClick={handleStartGame} hidden={hasGameStarted}>
                Start Quiz
            </button>

        </div>
    );
}



export default Lobby;