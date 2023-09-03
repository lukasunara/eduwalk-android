package hr.eduwalk.networking.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateWalkRequestBody(
    val title: String,
    val description: String?,
)

@Serializable
data class UpdateLocationScoreBody(
    @SerialName("userId") val userId: String,
    @SerialName("locationId") val locationId: Int,
    @SerialName("score") val score: Int?,
)
