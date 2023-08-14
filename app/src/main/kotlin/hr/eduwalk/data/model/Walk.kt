package hr.eduwalk.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Walk(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String? = null,
    @SerialName("creatorId") val creatorId: String? = null,
)
