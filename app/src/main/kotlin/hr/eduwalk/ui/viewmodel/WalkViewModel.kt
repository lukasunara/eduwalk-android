package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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

//    fun start() { }

    fun onStartWalkClicked(walkId: String) {
        viewModelScope.launch {
            val walk = handleErrorResponse(response = eduWalkRepository.getWalk(walkId = walkId)) ?: return@launch
            eventsFlow.emit(value = WalkEvent.StartWalk(walk = walk))
        }
    }
}
