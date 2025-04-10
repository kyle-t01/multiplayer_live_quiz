import { GlobalVars } from "../context/GlobalContext";
const TimerBar = () => {

    // global state
    const { timeLeft } = GlobalVars();
    return (
        <div className="timer-bar">
            <h2>{timeLeft}</h2>
        </div>
    );

}

export default TimerBar;