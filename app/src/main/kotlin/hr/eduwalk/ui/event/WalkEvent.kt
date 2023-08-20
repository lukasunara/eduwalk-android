package hr.eduwalk.ui.event

import hr.eduwalk.data.model.LocationWithScore

sealed interface WalkEvent {

    data class ShowLocations(val locationsWithScores: List<LocationWithScore>) : WalkEvent

    data class UpdateScore(val total: Int, val max: Int) : WalkEvent
}
