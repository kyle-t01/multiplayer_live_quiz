import { useEffect, useState, useRef, } from 'react';

function App() {
	const socketRef = useRef(null);
	const [playerName, setPlayerName] = useState("");
	const [hasJoined, setHasJoined] = useState(false);
	const [lobby, setLobby] = useState([]);

	// when the player joins the lobby, open connection to websocket
	const handlePlayerJoin = () => {
		if (!playerName.trim()) return;

		// attempt connection
		if (!socketRef.current) {
			socketRef.current = new WebSocket('ws://localhost:8080/quiz');
		}

		// establish connection
		socketRef.current.onopen = () => {
			console.log('websocket open!');
			sendGameEvent('join', playerName.trim());
		};

		// receive message
		socketRef.current.onmessage = (event) => {
			const message = JSON.parse(event.data);
			console.log("server sent msg:");
			console.log(message);

			// check if player was added to the lobby
			if (message.type == "JOIN") {
				setHasJoined(true);
				console.log("You have joined the lobby!");
			}

			// check if there was a lobby update
			if (message.type == "LOBBY_UPDATE") {
				setLobby(message.data)
				console.log("Updating current lobby!");
			}
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




	// ie: sendEvent(STRING, STRING)
	const sendGameEvent = (type, data) => {
		const gameEvent = {
			type: type.toUpperCase(),
			data: data,
		};
		socketRef.current.send(JSON.stringify(gameEvent));
		console.log(`sent a game event: ${type}: ${data}`);
	}

	const renderCurrentLobby = () => {
		return (
			<div className="lobby" hidden={!hasJoined}>
				<h2>Current Players</h2>
				{lobby.map((p, i) => renderPlayerCard(p, i))}
			</div>
		);
	}

	const renderPlayerCard = (p, i) => {
		return <p key={i}>{p.name} {p.qcorrect}</p>;
	}

	const renderJoinLobby = () => {
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
					maxLength={15}
				/>
				<div className="char-counter">
					{playerName.length} / {15}
				</div>
				<button className="button" onClick={handlePlayerJoin}>
					Join Lobby
				</button>
			</div>
		);
	}

	return (
		<div className="app">
			<h1>Welcome to the APP DEMO</h1>
			{renderCurrentLobby()}
			{renderJoinLobby()}

		</div>
	);
}

export default App;
