package hr.eduwalk.ui.state

import hr.eduwalk.data.model.Question

data class AddQuestionsUiState(
    val question: Question? = null,
    val questionIndex: Int? = null,
    val isContentVisible: Boolean = false,
    val isPrevButtonVisible: Boolean = false,
    val isNextButtonVisible: Boolean = false,
)
