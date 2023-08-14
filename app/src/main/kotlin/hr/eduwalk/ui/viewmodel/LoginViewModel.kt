package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.model.User
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.LoginEvent
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
) : BaseViewModel<Unit, LoginEvent>() {

    override val uiStateFlow = MutableStateFlow(value = Unit)
    override val eventsFlow = MutableStateFlow<LoginEvent?>(value = null)

    fun onLoginClicked(username: String) {
        viewModelScope.launch {
            val user = handleErrorResponse(
                response = eduWalkRepository.loginUser(user = User(username = username)),
            ) ?: return@launch
            eduWalkRepository.saveUserData(user = user)
            eventsFlow.emit(value = LoginEvent.FinishLogin)
        }
    }
}
