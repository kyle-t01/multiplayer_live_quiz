import { GlobalVars } from "../context/GlobalContext";


const QuestionDisplay = () => {

    // global state
    const { question, handleUserAnswer, userAnswer, isShowAnswer } = GlobalVars();


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


    if (!question) return;
    return (
        <div className='question'>
            <h3>{question.question}</h3>
            <div className='options'>{question.options.map((o, i) => renderOption(o, i))}</div>
        </div>

    );
}








export default QuestionDisplay;