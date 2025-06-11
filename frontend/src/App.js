import CountdownText from './components/CountdownText';
import InputBox from './components/InputBox';
import Lobby from './components/Lobby';
import QuestionDisplay from './components/QuestionDisplay';
import TimerBar from './components/TimerBar';

function App() {

	return (
		<div className="app">
			<h2>Welcome to Custom Trivia Night!</h2>
			<Lobby />
			<InputBox />
			<TimerBar />
			<CountdownText />
			<QuestionDisplay />
		</div>
	);
}

export default App;
