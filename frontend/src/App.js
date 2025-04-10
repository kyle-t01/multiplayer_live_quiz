import CountdownText from './components/CountdownText';
import InputBox from './components/InputBox';
import Lobby from './components/Lobby';
import QuestionDisplay from './components/QuestionDisplay';
import TimerBar from './components/TimerBar';

function App() {

	return (
		<div className="app">
			<h1>Welcome to Mutiplayer Live Quiz!</h1>
			<p>It is a project built with Kotlin + SpringBoot WebSockets</p>
			<Lobby />
			<InputBox />
			<TimerBar />
			<CountdownText />
			<QuestionDisplay />
		</div>
	);
}

export default App;
