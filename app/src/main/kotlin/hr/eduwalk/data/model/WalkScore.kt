package hr.eduwalk.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalkScore(
    @SerialName("userId") val username: String,
    @SerialName("walkId") val walkId: String,
    @SerialName("score") val score: Int? = null,
)
