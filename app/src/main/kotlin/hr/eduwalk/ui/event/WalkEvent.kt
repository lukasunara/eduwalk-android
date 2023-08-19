package hr.eduwalk.ui.event

import hr.eduwalk.data.model.Walk

sealed interface WalkEvent {

    data class StartWalk(val walk: Walk) : WalkEvent
}
