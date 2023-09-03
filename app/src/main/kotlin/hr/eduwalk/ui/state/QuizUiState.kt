package hr.eduwalk.ui.state

import hr.eduwalk.data.model.Question

data class QuizUiState(
    val question: Question? = null,
    val questionNumber: Int? = null,
)
