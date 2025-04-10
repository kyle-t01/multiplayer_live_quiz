import { GlobalVars } from "../context/GlobalContext";
const TimerBar = () => {

    // global state
    const { timeLeft, totalTime } = GlobalVars();



    return (
        <div className="timer-bar">

            <div className="bar"
                style={{ width: `${(timeLeft / totalTime) * 100}%` }}>
            </div>

        </div>


    );

}

export default TimerBar;