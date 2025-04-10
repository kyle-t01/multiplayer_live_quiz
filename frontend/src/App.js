import { useEffect, useState, useRef, } from 'react';
import { GlobalVars } from './context/GlobalContext';

function App() {

	// global variables
	const {
		socketRef,
		playerName, setPlayerName,
		hasJoined, setHasJoined,
		lobby, setLobby,
		hasGameStarted, setHasGameStarted,
		question, setQuestion,
		userAnswer, setUserAnswer,
		isShowAnswer, setIsShowAnswer
	} = GlobalVars();

	// when the player joins the lobby, open connection to websocket
	const handlePlayerJoin = () => {
		if (!playerName.trim()) return;

		// close existing socket
		if (socketRef.current) {
			socketRef.current.close();
		}
		// attempt connection
		if (!socketRef.current || socketRef.current.readyState === WebSocket.CLOSED) {
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
				setLobby(message.data);
				console.log("Updating current lobby!");
			}

			// check if game has started
			if (message.type == "START") {
				setHasGameStarted(true);
				console.log("Starting or Joining an existing game!");
			}

			// check if game has ended
			if (message.type == "END") {
				setHasGameStarted(false);
				setUserAnswer(null);
				setIsShowAnswer(false)
				setQuestion(null)
				console.log("Game has ended!");
			}

			// check if game has sent a question
			if (message.type == "QUESTION") {
				setIsShowAnswer(false);
				setQuestion(message.data);
				setUserAnswer(null);
				console.log("GOT A QUESTION");
			}

			// check if game has want to show the current answer
			if (message.type == "SHOW") {
				setIsShowAnswer(true);
				console.log("SHOWING CURRENT ANSWERS");
			}

			// check if player has been KICKED
			if (message.type == "KICK") {
				setHasGameStarted(false);
				setHasJoined(false)
				// check if player has been KICKED
				console.log("You were KICKED from the game!");
				alert("A game is already in progress, wait for it to finish before joining!");
				socketRef.current.close();

			}

			// check if player answer has been received
			if (message.type == "ANSWER") {
				console.log("Your answer was received!");
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

	const handleStartGame = () => {
		sendGameEvent("START", "");
	}


	// ie: sendEvent(STRING, Object)
	const sendGameEvent = (type, data) => {
		const gameEvent = {
			type: type.toUpperCase(),
			data: data,
		};
		socketRef.current.send(JSON.stringify(gameEvent));
		console.log(`sent a game event: ${type}: ${data}`);
	}

	const renderCurrentLobby = () => {
		if (!hasJoined) return;
		return (
			<div>
				<h2>Active Players</h2>
				<div className="lobby">
					{lobby.map((p, i) => renderPlayerCard(p, i))}
				</div>

				<button className="button" onClick={handleStartGame} hidden={hasGameStarted}>
					Start Quiz
				</button>
			</div>
		);
	}

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

	const renderQuestion = () => {
		if (!question) return;
		return (
			<div className='question'>
				<h3>{question.question}</h3>
				<div className='options'>{question.options.map((o, i) => renderOption(o, i))}</div>
			</div>

		);
	}

	const renderOption = (o, i) => {
		// render selected user answer
		const isSelectedAnswer = (i == userAnswer)
		const optionClassName = (isSelectedAnswer) ? "selected-option" : "option";

		// if answer was revealed then highlight the correct answer
		const isCorrectAnswer = (i == question?.answers[0])
		if (isCorrectAnswer && isShowAnswer) {
			return (
				<div key={i} className="correct-option" onClick={() => handleUserAnswer(i)}>
					<p>{o}</p>
				</div>
			);

		}

		// else show selected option
		return (
			<div key={i} className={optionClassName} onClick={() => handleUserAnswer(i)}>
				<p>{o}</p>
			</div>
		);
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


	return (
		<div className="app">
			<h1>Welcome to the APP DEMO</h1>
			{renderCurrentLobby()}
			{renderJoinLobby()}
			{renderQuestion()}

		</div>
	);
}

export default App;
