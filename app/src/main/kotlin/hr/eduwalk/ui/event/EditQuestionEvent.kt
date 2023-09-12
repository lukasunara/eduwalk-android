package hr.eduwalk.ui.event

import hr.eduwalk.data.model.Question

sealed interface EditQuestionEvent {

    data class FinishEditQuestion(val question: Question) : EditQuestionEvent
}
