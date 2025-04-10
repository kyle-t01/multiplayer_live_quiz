import { useEffect, useState, useRef, } from 'react';
import { GlobalVars } from './context/GlobalContext';
import InputBox from './components/InputBox';
import Lobby from './components/Lobby';
import QuestionDisplay from './components/QuestionDisplay';

function App() {

	return (
		<div className="app">
			<h1>Welcome to Mutiplayer Live Quiz!</h1>
			<p>It is a project built with Kotlin + SpringBoot WebSockets</p>
			<Lobby />
			<InputBox />
			<QuestionDisplay />
		</div>
	);
}

export default App;
