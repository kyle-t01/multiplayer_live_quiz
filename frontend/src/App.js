import { useEffect, useState, useRef, } from 'react';
import { GlobalVars } from './context/GlobalContext';
import InputBox from './components/InputBox';

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
		isShowAnswer, setIsShowAnswer,
		sendGameEvent,
		handlePlayerJoin,
	} = GlobalVars();


	const handleStartGame = () => {
		sendGameEvent("START", "");
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
			<InputBox />
			{renderQuestion()}

		</div>
	);
}

export default App;
