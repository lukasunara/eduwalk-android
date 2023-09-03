package hr.eduwalk.ui.event

sealed interface QuizEvent {

    object StartQuiz : QuizEvent

    data class FinishQuiz(val correctAnswers: Int, val max: Int, val isNewBestScore: Boolean) : QuizEvent
}
