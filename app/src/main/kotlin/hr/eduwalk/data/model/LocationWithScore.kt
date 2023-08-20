package hr.eduwalk.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationWithScore(
    @SerialName("location") val location: Location,
    @SerialName("score") val score: Int? = null,
)
