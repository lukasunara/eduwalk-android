package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.model.Location
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.EditLocationEvent
import hr.eduwalk.ui.state.EditLocationUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditLocationViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<EditLocationUiState, EditLocationEvent>() {

    override val uiStateFlow = MutableStateFlow(value = EditLocationUiState())
    override val eventsFlow = MutableStateFlow<EditLocationEvent?>(value = null)

    fun start(location: Location) {
        uiStateFlow.update { it.copy(location = location) }
    }

    fun onEditLocationClicked(locationTitle: String, locationDescription: String?, imageBase64: String?, thresholdDistance: Int) {
        val oldLocation = uiStateFlow.value.location ?: return

        val newLocation = oldLocation.copy(
            title = locationTitle,
            description = locationDescription,
            imageBase64 = imageBase64,
            thresholdDistance = thresholdDistance,
        )
        if (oldLocation == newLocation) return

        viewModelScope.launch {
            when (newLocation.id) {
                -1 -> {
                    val location = handleErrorResponse(
                        response = eduWalkRepository.createLocation(location = newLocation),
                    ) ?: return@launch
                    uiStateFlow.update { it.copy(location = location) }
                }
                else -> {
                    handleErrorResponse(response = eduWalkRepository.updateLocation(location = newLocation)) ?: return@launch
                    uiStateFlow.update { it.copy(location = newLocation) }
                }
            }
        }
    }

    fun onDeleteLocationClicked() {
        val locationId = uiStateFlow.value.location?.id ?: return
        viewModelScope.launch {
            handleErrorResponse(response = eduWalkRepository.deleteLocation(locationId = locationId)) ?: return@launch
            eventsFlow.emit(value = EditLocationEvent.FinishDeleteLocation(locationId = locationId))
        }
    }

    fun onAddQuestionClicked(locationTitle: String, locationDescription: String, imageBase64: Nothing?, thresholdDistance: Int) {
        onEditLocationClicked(locationTitle, locationDescription, imageBase64, thresholdDistance)
        // TODO
    }
}
