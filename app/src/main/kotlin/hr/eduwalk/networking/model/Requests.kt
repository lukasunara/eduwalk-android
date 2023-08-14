package hr.eduwalk.networking.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateWalkRequestBody(
    val title: String,
    val description: String?,
)
