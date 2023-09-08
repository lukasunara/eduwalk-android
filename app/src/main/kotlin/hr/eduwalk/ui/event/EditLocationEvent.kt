package hr.eduwalk.ui.event

sealed interface EditLocationEvent {

    data class FinishDeleteLocation(val locationId: Long) : EditLocationEvent
    data class NavigateToAddQuestions(val locationId: Long, val locationTitle: String) : EditLocationEvent
}
