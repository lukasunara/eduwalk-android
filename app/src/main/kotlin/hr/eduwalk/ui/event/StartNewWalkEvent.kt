package hr.eduwalk.ui.event

import hr.eduwalk.data.model.Walk

sealed interface StartNewWalkEvent {

    data class StartWalk(val walk: Walk) : StartNewWalkEvent

    data class ShowDefaultWalks(val walks: List<Walk>) : StartNewWalkEvent
}
