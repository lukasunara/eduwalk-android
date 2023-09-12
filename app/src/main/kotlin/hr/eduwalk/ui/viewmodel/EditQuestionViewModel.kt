package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.model.Question
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.EditQuestionEvent
import hr.eduwalk.ui.state.EditQuestionUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditQuestionViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<EditQuestionUiState, EditQuestionEvent>() {

    override val uiStateFlow = MutableStateFlow(value = EditQuestionUiState())
    override val eventsFlow = MutableStateFlow<EditQuestionEvent?>(value = null)

    fun start(question: Question) {
        uiStateFlow.update { it.copy(question = question) }
    }

    fun onEditQuestionClicked(
        questionText: String,
        correctAnswer: String,
        answer2: String?,
        answer3: String?,
        answer4: String?,
        answer5: String?,
    ) {
        val oldQuestion = uiStateFlow.value.question ?: return

        val newQuestion = oldQuestion.copy(
            questionText = questionText,
            answers = listOfNotNull(correctAnswer, answer2, answer3, answer4, answer5),
            correctAnswer = correctAnswer,
        )
        if (oldQuestion == newQuestion) return

        viewModelScope.launch {
            when (newQuestion.id) {
                -1L -> {
                    val question = handleErrorResponse(
                        response = eduWalkRepository.createQuestion(question = newQuestion),
                    ) ?: return@launch
                    uiStateFlow.update { it.copy(question = question) }
                }
                else -> {
                    handleErrorResponse(response = eduWalkRepository.updateQuestion(question = newQuestion)) ?: return@launch
                    uiStateFlow.update { it.copy(question = newQuestion) }
                }
            }
        }
    }
}
