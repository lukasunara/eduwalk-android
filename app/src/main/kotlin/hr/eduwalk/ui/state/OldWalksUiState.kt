package hr.eduwalk.ui.state

import hr.eduwalk.data.model.WalkWithScore

data class OldWalksUiState(
    val walksWithScores: List<WalkWithScore> = emptyList(),
)
