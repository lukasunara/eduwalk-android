package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.state.MyWalksUiState
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MyWalksViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<MyWalksUiState, Unit>() {

    override val uiStateFlow = MutableStateFlow(value = MyWalksUiState())
    override val eventsFlow = MutableStateFlow<Unit?>(value = null)

    fun start() = getMyWalks()

    private fun getMyWalks() {
        viewModelScope.launch {
            val myWalks = handleErrorResponse(response = eduWalkRepository.getMyWalks()) ?: return@launch
            uiStateFlow.update { it.copy(walks = myWalks) }
        }
    }
}
