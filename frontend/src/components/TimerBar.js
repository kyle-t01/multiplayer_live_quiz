import { GlobalVars } from "../context/GlobalContext";
const TimerBar = () => {

    // global state
    const { timeLeft, totalTime } = GlobalVars();



    return (
        <div className="timer-bar">
            <h2>{timeLeft} / {totalTime}</h2>
        </div>
    );

}

export default TimerBar;