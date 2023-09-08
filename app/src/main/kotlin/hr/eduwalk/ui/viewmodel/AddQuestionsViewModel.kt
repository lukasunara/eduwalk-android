package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.model.Question
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.AddQuestionsEvent
import hr.eduwalk.ui.state.AddQuestionsUiState
import javax.inject.Inject
import kotlin.properties.Delegates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddQuestionsViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<AddQuestionsUiState, AddQuestionsEvent>() {

    override val uiStateFlow = MutableStateFlow(value = AddQuestionsUiState())
    override val eventsFlow = MutableStateFlow<AddQuestionsEvent?>(value = null)

    private var questions: MutableList<Question>? = null

    private var locationId: Long by Delegates.notNull()

    fun start(locationId: Long) {
        this.locationId = locationId
        getQuestions()
    }

    fun onNextQuestionClicked() {
        val questions = questions ?: return
        val questionIndex = uiStateFlow.value.questionIndex ?: return
        if (questionIndex == questions.size - 1) return

        uiStateFlow.update {
            it.copy(
                question = questions[questionIndex + 1],
                questionIndex = questionIndex + 1,
                isPrevButtonVisible = true,
                isNextButtonVisible = questionIndex < questions.size - 1,
            )
        }
    }

    fun onPreviousQuestionClicked() {
        val questions = questions ?: return
        val questionIndex = uiStateFlow.value.questionIndex ?: return
        if (questionIndex == 0) return

        uiStateFlow.update {
            it.copy(
                question = questions[questionIndex - 1],
                questionIndex = questionIndex - 1,
                isPrevButtonVisible = questionIndex > 0,
                isNextButtonVisible = true,
            )
        }
    }

    fun onEditQuestionClicked() {
        viewModelScope.launch {
            eventsFlow.emit(value = AddQuestionsEvent.NavigateToEditQuestion(question = uiStateFlow.value.question))
        }
    }

    fun onDeleteQuestionClicked() {
        val questions = questions ?: return
        val question = uiStateFlow.value.question ?: return
        val questionIndex = uiStateFlow.value.questionIndex ?: return

        viewModelScope.launch {
            handleErrorResponse(response = eduWalkRepository.deleteQuestion(questionId = question.id)) ?: return@launch

            questions.remove(element = question)

            val newQuestion = questions.getOrNull(index = questionIndex - 1) ?: questions.getOrNull(index = questionIndex)
            val newIndex = newQuestion?.let { questions.indexOf(element = it) }
            uiStateFlow.update {
                it.copy(
                    question = newQuestion,
                    questionIndex = newIndex,
                    isPrevButtonVisible = newIndex != null && newIndex > 0,
                    isNextButtonVisible = newIndex != null && newIndex < questions.size - 1,
                )
            }
        }
    }

    private fun getQuestions() {
        viewModelScope.launch {
            val questions = handleErrorResponse(
                response = eduWalkRepository.getLocationQuestions(locationId = locationId),
            ) ?: return@launch
            this@AddQuestionsViewModel.questions = questions.toMutableList()
            uiStateFlow.update {
                val question = questions.firstOrNull()
                it.copy(
                    question = question,
                    questionIndex = 0,
                    isContentVisible = true,
                    isPrevButtonVisible = false,
                    isNextButtonVisible = question != null && questions.size > 1,
                )
            }
        }
    }
}
