package hr.eduwalk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Question(
    @SerialName("id") val id: Long,
    @SerialName("questionText") val questionText: String,
    @SerialName("answers") var answers: List<String>,
    @SerialName("correctAnswer") val correctAnswer: String,
    @SerialName("locationId") val locationId: Long,
) : Parcelable
