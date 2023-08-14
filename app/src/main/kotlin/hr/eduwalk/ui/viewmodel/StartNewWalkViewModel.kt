package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.StartNewWalkEvent
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class StartNewWalkViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<Unit, StartNewWalkEvent>() {

    override val uiStateFlow = MutableStateFlow(value = Unit)
    override val eventsFlow = MutableStateFlow<StartNewWalkEvent?>(value = null)

    fun start() {
        getDefaultWalks()
    }

    fun onStartWalkClicked(walkId: String) {
        viewModelScope.launch {
            val walk = handleErrorResponse(response = eduWalkRepository.getWalk(walkId = walkId)) ?: return@launch
            eventsFlow.emit(value = StartNewWalkEvent.StartWalk(walk = walk))
        }
    }

    fun onDefaultWalkClicked(walkId: String) = onStartWalkClicked(walkId = walkId)

    private fun getDefaultWalks() {
        viewModelScope.launch {
            val defaultWalks = handleErrorResponse(response = eduWalkRepository.getDefaultWalks()) ?: return@launch
            eventsFlow.emit(value = StartNewWalkEvent.ShowDefaultWalks(walks = defaultWalks))
        }
    }
}
