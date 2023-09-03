package hr.eduwalk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class LocationWithScore(
    @SerialName("location") val location: Location,
    @SerialName("score") var score: Int? = null,
) : Parcelable
