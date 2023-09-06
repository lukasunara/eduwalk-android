package hr.eduwalk.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Location(
    @SerialName("id") val id: Long,
    @SerialName("latitude") val latitude: Double,
    @SerialName("longitude") val longitude: Double,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String?,
    @SerialName("imageBase64") val imageBase64: String?,
    @SerialName("thresholdDistance") val thresholdDistance: Int,
    @SerialName("walkId") val walkId: String,
) : Parcelable
