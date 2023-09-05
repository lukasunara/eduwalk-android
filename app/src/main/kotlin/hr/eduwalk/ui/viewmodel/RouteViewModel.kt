package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.model.Location
import hr.eduwalk.data.model.Walk
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.RouteEvent
import hr.eduwalk.ui.state.RouteUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<RouteUiState, RouteEvent>() {

    override val uiStateFlow = MutableStateFlow(value = RouteUiState())
    override val eventsFlow = MutableStateFlow<RouteEvent?>(value = null)

    fun start(walk: Walk) {
        uiStateFlow.update { it.copy(walk = walk) }
        getWalkLocations()
    }

//    fun onNewLocationScoreReceived(locationId: Int, newLocationScore: Int) {
//        val oldLocationScore = locations.first { it.location.id == locationId }.score
//        if (oldLocationScore == null || newLocationScore > oldLocationScore) {
//            viewModelScope.launch {
//                locations.forEach {
//                    if (it.location.id == locationId) {
//                        it.score = newLocationScore
//                    }
//                }
//                updateWalkScore()
//                handleErrorResponse(
//                    response = eduWalkRepository.createOrUpdateWalkScore(
//                        walkId = walkId,
//                        newScore = locations.sumOf { it.score ?: 0 },
//                    ),
//                ) ?: return@launch
//            }
//        }
//    }

    fun onDestroyView() {
        uiStateFlow.value = RouteUiState()
    }

    fun onEditWalkInfoClicked() {
        val walk = uiStateFlow.value.walk ?: return
        viewModelScope.launch {
            eventsFlow.emit(value = RouteEvent.NavigateToEditWalkInfo(walk = walk))
        }
    }

    fun onDeleteWalkClicked() {
        val walk = uiStateFlow.value.walk ?: return
        viewModelScope.launch {
            handleErrorResponse(response = eduWalkRepository.deleteWalk(walkId = walk.id)) ?: return@launch
            eventsFlow.emit(value = RouteEvent.FinishRouteFragment)
        }
    }

    fun onNewWalkInfoReceived(walk: Walk) {
        uiStateFlow.update { it.copy(walk = walk) }
    }

    fun onMapLoaded() {
        viewModelScope.launch { showUpdatedLocations() }
    }

    fun onMarkerDragged(oldLocation: Location, newLatitude: Double, newLongitude: Double) {
        viewModelScope.launch {
            val newLocation = oldLocation.copy(latitude = newLatitude, longitude = newLongitude)

            handleErrorResponse(response = eduWalkRepository.updateLocation(location = newLocation)) ?: return@launch

            uiStateFlow.value.locations.apply {
                remove(oldLocation)
                add(newLocation)
            }
            showUpdatedLocations()
        }
    }

    private fun getWalkLocations() {
        val walkId = uiStateFlow.value.walk?.id ?: return

        viewModelScope.launch {
            val locations = handleErrorResponse(
                response = eduWalkRepository.getWalkLocations(walkId = walkId),
            )?.toMutableList() ?: return@launch

            uiStateFlow.update { it.copy(locations = locations) }
        }
    }

    private suspend fun showUpdatedLocations() {
        eventsFlow.emit(value = RouteEvent.ShowLocations(locations = uiStateFlow.value.locations))
    }
}
