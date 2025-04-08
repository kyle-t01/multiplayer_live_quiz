import logo from './logo.svg';
import './App.css';

import { useEffect, useState, useRef, } from 'react';

function App() {
	const socketRef = useRef(null);
	const [playerName, setPlayerName] = useState("");

	// attempt connection with web socket on first load
	useEffect(() => {

	}, [])

	// when the player joins the lobby, open connection to websocket
	const handlePlayerJoin = () => {
		if (!playerName.trim()) return;

		// attempt connection
		socketRef.current = new WebSocket('ws://localhost:8080/quiz');

		// establish connection
		socketRef.current.onopen = () => {
			console.log('websocket open!');
			sendGameEvent('join', playerName.trim());
		};

		// receive message
		socketRef.current.onmessage = (event) => {
			const message = JSON.parse(event.data);
			console.log(message)
		};

		// error
		socketRef.current.onerror = (e) => {
			console.log('websocket error!', e);
		};

		// disconnect
		socketRef.current.onclose = () => {
			console.log('websocket disconnected!');
		};
		return () => socketRef.current.close();
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

	return (
		<div className="app">
			<h1>Welcome to the APP DEMO</h1>

			<h2>Join Game Here</h2>



			<div className="input-box">
				<textarea
					className="input"
					type="text"
					placeholder="Username..."
					value={playerName}
					onChange={(e) => { setPlayerName(e.target.value) }}
					rows={1}
					maxLength={12}
				/>
				<div className="char-counter">
					{playerName.length} / {12}
				</div>
				<button className="button" onClick={handlePlayerJoin}>
					Join Lobby
				</button>
			</div>
		</div>
	);
}

export default App;
