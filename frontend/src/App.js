import { useEffect, useState, useRef, } from 'react';
import { GlobalVars } from './context/GlobalContext';
import InputBox from './components/InputBox';
import Lobby from './components/Lobby';
import QuestionDisplay from './components/QuestionDisplay';

function App() {

	return (
		<div className="app">
			<h1>Welcome to the APP DEMO</h1>
			<Lobby />
			<InputBox />
			<QuestionDisplay />

		</div>
	);
}

export default App;
