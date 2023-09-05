package hr.eduwalk.ui.event

import hr.eduwalk.data.model.Location
import hr.eduwalk.data.model.Walk

sealed interface RouteEvent {

    data object FinishRouteFragment : RouteEvent

    data class NavigateToEditWalkInfo(val walk: Walk) : RouteEvent
    data class ShowLocations(val locations: List<Location>) : RouteEvent
}
