package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.HomeEvent
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<Unit, HomeEvent>() {

    override val uiStateFlow = MutableStateFlow(value = Unit)
    override val eventsFlow = MutableStateFlow<HomeEvent?>(value = null)

    fun onLogoutClicked() {
        viewModelScope.launch {
            eduWalkRepository.logoutUser()
            eventsFlow.emit(value = HomeEvent.UserLogout)
        }
    }
}
