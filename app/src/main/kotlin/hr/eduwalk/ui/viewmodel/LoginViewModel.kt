package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.eduwalk.data.sharedprefs.SharedPreferencesRepository
import hr.eduwalk.data.model.User
import hr.eduwalk.networking.EduWalkRepository
import hr.eduwalk.ui.event.LoginEvent
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val eduWalkRepository: EduWalkRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
) : BaseViewModel<Unit, LoginEvent>() {

    private var currentUsername: String? = null

    override val uiStateFlow = MutableStateFlow(value = Unit)
    override val eventsFlow = MutableStateFlow<LoginEvent?>(value = null)

    fun onLoginClicked(username: String) {
        currentUsername = username
        viewModelScope.launch {
            val user = handleErrorResponse(
                response = eduWalkRepository.login(user = User(username = username)),
            ) ?: return@launch
            sharedPreferencesRepository.setObject(key = SharedPreferencesRepository.KEY_USER, value = user)
            eventsFlow.emit(value = LoginEvent.FinishLogin)
        }
    }
}
