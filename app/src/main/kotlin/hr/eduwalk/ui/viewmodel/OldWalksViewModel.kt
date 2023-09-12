package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.state.OldWalksUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OldWalksViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<OldWalksUiState, Unit>() {

    override val uiStateFlow = MutableStateFlow(value = OldWalksUiState())
    override val eventsFlow = MutableStateFlow<Unit?>(value = null)

    fun start() {
        getWalksWithScores()
    }

    private fun getWalksWithScores() {
        viewModelScope.launch {
            val walksWithScores = handleErrorResponse(response = eduWalkRepository.getWalksWithScores()) ?: return@launch
            uiStateFlow.update {
                it.copy(walksWithScores = walksWithScores)
            }
        }
    }

    fun onDestroyView() {
        uiStateFlow.value = OldWalksUiState()
    }
}
