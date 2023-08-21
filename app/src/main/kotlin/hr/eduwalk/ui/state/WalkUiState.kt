package hr.eduwalk.ui.state

import hr.eduwalk.data.model.LocationWithScore

data class WalkUiState(
    val maxScore: Int = 0,
    val userScoreTotal: Int = 0,
    val locationsWithScores: List<LocationWithScore> = emptyList(),
)
