package hr.eduwalk.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.eduwalk.networking.model.ApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<UiState, EventsState> : ViewModel() {

    abstract val uiStateFlow: StateFlow<UiState>
    abstract val eventsFlow: StateFlow<EventsState?>

    val errorMessageFlow = MutableStateFlow<String?>(value = null)

    fun onEventConsumed() {
        viewModelScope.launch {
            (eventsFlow as MutableStateFlow).emit(value = null)
        }
    }

    fun onErrorConsumed() {
        viewModelScope.launch { errorMessageFlow.emit(value = null) }
    }

    protected fun <T> handleErrorResponse(response: ApiResponse<T>): T? = when (response) {
        is ApiResponse.Error -> {
            viewModelScope.launch { errorMessageFlow.emit(value = response.errorMessage) }
            null
        }
        is ApiResponse.Success -> response.data
    }
}
