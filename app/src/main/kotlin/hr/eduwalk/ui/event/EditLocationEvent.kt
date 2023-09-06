package hr.eduwalk.ui.event

sealed interface EditLocationEvent {

    data class FinishDeleteLocation(val locationId: Long) : EditLocationEvent
}
