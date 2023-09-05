package hr.eduwalk.ui.state

import hr.eduwalk.data.model.Location
import hr.eduwalk.data.model.Walk

data class RouteUiState(
    val walk: Walk? = null,
    val locations: MutableList<Location> = mutableListOf(),
)
