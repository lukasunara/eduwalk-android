package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.model.Question
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.QuizEvent
import hr.eduwalk.ui.state.QuizUiState
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<QuizUiState, QuizEvent>() {

    override val uiStateFlow = MutableStateFlow(value = QuizUiState())
    override val eventsFlow = MutableStateFlow<QuizEvent?>(value = null)

    private var questions: List<Question>? = null
    private var correctAnswers = 0

    private var locationId: Long by Delegates.notNull()
    private var currentScore: Int by Delegates.notNull()

    fun start(locationId: Long, currentScore: Int) {
        this.locationId = locationId
        this.currentScore = currentScore
        getQuestions()
    }

    fun onStartQuizClicked() {
        viewModelScope.launch {
            eventsFlow.emit(value = QuizEvent.StartQuiz)
        }
    }

    fun onAnswerSelected(isCorrect: Boolean) {
        if (isCorrect) {
            correctAnswers++
        }
    }

    fun onNextQuestionClicked() {
        val questions = questions ?: return
        val questionIndex = uiStateFlow.value.questionIndex ?: return

        if (questionIndex + 1 < EduWalkRepository.NUMBER_OF_QUESTIONS_PER_QUIZ) {
            uiStateFlow.update { it.copy(question = questions[questionIndex + 1], questionIndex = questionIndex + 1) }
        } else {
            viewModelScope.launch {
                val isNewBestScore = correctAnswers > currentScore
                if (isNewBestScore) {
                    handleErrorResponse(
                        response = eduWalkRepository.updateLocationScore(locationId = locationId, newBestScore = correctAnswers),
                    ) ?: return@launch
                }
                eventsFlow.emit(
                    QuizEvent.FinishQuiz(
                        correctAnswers = correctAnswers,
                        max = EduWalkRepository.NUMBER_OF_QUESTIONS_PER_QUIZ,
                        isNewBestScore = isNewBestScore,
                    )
                )
            }
        }
    }

    private fun getQuestions() {
        viewModelScope.launch {
            val questions = handleErrorResponse(
                response = eduWalkRepository.getLocationQuestions(locationId = locationId),
            ) ?: return@launch

            val threeQuestions = if (questions.size <= 3) questions else questions.shuffled().subList(0, 3)
            threeQuestions.forEach { it.answers = it.answers.shuffled() }

            this@QuizViewModel.questions = threeQuestions
            uiStateFlow.update { it.copy(question = threeQuestions.firstOrNull(), questionIndex = 0) }
        }
    }
}
