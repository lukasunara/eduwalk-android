package hr.eduwalk.ui.event

sealed interface HomeEvent {

    object UserLogout : HomeEvent
}
