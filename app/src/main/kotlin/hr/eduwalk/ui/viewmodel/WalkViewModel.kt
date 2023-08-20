package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.model.LocationWithScore
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.WalkEvent
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<Unit, WalkEvent>() {

    override val uiStateFlow = MutableStateFlow(value = Unit)
    override val eventsFlow = MutableStateFlow<WalkEvent?>(value = null)

    private var locationsWithScores = listOf<LocationWithScore>()

    fun start(walkId: String) {
        getWalkLocationsWithScores(walkId = walkId)
    }

    private fun getWalkLocationsWithScores(walkId: String) {
        viewModelScope.launch {
            locationsWithScores = handleErrorResponse(response = eduWalkRepository.getLocationsWithScores(walkId = walkId)) ?: return@launch
            eventsFlow.emit(value = WalkEvent.ShowLocations(locationsWithScores = locationsWithScores))
            updateWalkScore()
        }
    }

    private suspend fun updateWalkScore() {
        eventsFlow.emit(
            value = WalkEvent.UpdateScore(
                total = locationsWithScores.sumOf { it.score ?: 0 },
                max = EduWalkRepository.NUMBER_OF_QUESTIONS_PER_QUIZ * locationsWithScores.size,
            )
        )
    }
}
