package hr.eduwalk.ui.event

import hr.eduwalk.data.model.Question

sealed interface AddQuestionsEvent {

    data class NavigateToEditQuestion(val question: Question?) : AddQuestionsEvent
}
