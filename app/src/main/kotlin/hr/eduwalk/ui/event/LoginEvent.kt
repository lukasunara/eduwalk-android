package hr.eduwalk.ui.event

sealed interface LoginEvent {

    object FinishLogin : LoginEvent
}
