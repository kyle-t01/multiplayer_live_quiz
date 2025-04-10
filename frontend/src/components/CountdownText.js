import { GlobalVars } from "../context/GlobalContext";


const CountdownText = () => {
    // global state
    const { timeLeft, totalTime } = GlobalVars();

    return (
        <div className="countdown-text">
            <div style={{ width: `${(timeLeft / totalTime) * 100}%`, transition: `width 0.25s linear` }}>
            </div>
            <p>{timeLeft / 1000}</p>
        </div>
    );
}



export default CountdownText;