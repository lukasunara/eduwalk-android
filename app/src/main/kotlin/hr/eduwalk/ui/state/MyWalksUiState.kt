package hr.eduwalk.ui.state

import hr.eduwalk.data.model.Walk

data class MyWalksUiState(
    val walks: List<Walk> = emptyList(),
)
