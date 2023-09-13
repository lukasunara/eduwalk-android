package hr.eduwalk.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalkWithScore(
    @SerialName("walk") val walk: Walk,
    @SerialName("score") var score: Int,
    @SerialName("maxScore") var maxScore: Int = 0,
)
