package hr.eduwalk.ui.event

import hr.eduwalk.data.model.Walk

sealed interface EditWalkEvent {

    data class FinishCreatingWalk(val walk: Walk) : EditWalkEvent
    data class FinishEditingWalkInfo(val walk: Walk) : EditWalkEvent
    data class SetupWalkId(val walkId: String) : EditWalkEvent
}
