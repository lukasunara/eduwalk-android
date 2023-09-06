package hr.eduwalk.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    @SerialName("id") val id: Long,
    @SerialName("questionText") val questionText: String,
    @SerialName("answers") val answers: List<String>,
    @SerialName("correctAnswer") val correctAnswer: String,
    @SerialName("locationId") val locationId: Long,
)
