package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.model.LocationWithScore
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.WalkEvent
import hr.eduwalk.ui.state.WalkUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WalkViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<WalkUiState, WalkEvent>() {

    override val uiStateFlow = MutableStateFlow(value = WalkUiState())
    override val eventsFlow = MutableStateFlow<WalkEvent?>(value = null)

    private var locationsWithScores = listOf<LocationWithScore>()

    private lateinit var walkId: String

    fun start(walkId: String) {
        this.walkId = walkId
        getWalkLocationsWithScores()
    }

    fun onShowLeaderboardClicked() {
        viewModelScope.launch {
            val walkScores = handleErrorResponse(response = eduWalkRepository.getTop5WalkScores(walkId = walkId)) ?: return@launch
            eventsFlow.emit(
                value = WalkEvent.ShowLeaderboard(
                    walkScores = walkScores,
                    currentUserScore = uiStateFlow.value.userScoreTotal,
                    currentUserUsername = eduWalkRepository.getUser()!!.username,
                )
            )
        }
    }

    private fun getWalkLocationsWithScores() {
        viewModelScope.launch {
            locationsWithScores = handleErrorResponse(response = eduWalkRepository.getLocationsWithScores(walkId = walkId)) ?: return@launch
            uiStateFlow.update {
                it.copy(locationsWithScores = locationsWithScores)
            }
            updateWalkScore()
        }
    }

    private fun updateWalkScore() {
        uiStateFlow.update {
            it.copy(
                maxScore = EduWalkRepository.NUMBER_OF_QUESTIONS_PER_QUIZ * locationsWithScores.size,
                userScoreTotal = locationsWithScores.sumOf { it.score ?: 0 },
            )
        }
    }
}
