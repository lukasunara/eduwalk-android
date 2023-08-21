package hr.eduwalk.ui.event

import hr.eduwalk.data.model.WalkScore

sealed interface WalkEvent {

    data class ShowLeaderboard(
        val walkScores: List<WalkScore>,
        val currentUserScore: Int,
        val currentUserUsername: String,
    ) : WalkEvent
}
