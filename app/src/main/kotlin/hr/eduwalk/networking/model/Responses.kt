package hr.eduwalk.networking.model

import hr.eduwalk.data.model.LocationWithScore
import hr.eduwalk.data.model.User
import hr.eduwalk.data.model.Walk
import hr.eduwalk.data.model.WalkScore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("user") val user: User? = null,
    @SerialName("error") val error: ResponseError? = null,
)

//@Serializable
//data class LocationQuestionsResponse(
//    val questions: List<Question>? = null,
//    val error: ResponseError? = null,
//)
//
//@Serializable
//data class LocationScoreResponse(
//    val locationScore: LocationScore? = null,
//    val error: ResponseError? = null,
//)
//
@Serializable
data class LocationsWithScoresResponse(
    @SerialName("locationsWithScores") val locationsWithScores: List<LocationWithScore>? = null,
    @SerialName("error") val error: ResponseError? = null,
)

//@Serializable
//data class WalkScoreResponse(
//    @SerialName("walkScore") val walkScore: WalkScore? = null,
//    @SerialName("error") val error: ResponseError? = null,
//)

@Serializable
data class WalkScoreTop5Response(
    val walkScores: List<WalkScore>? = null,
    val error: ResponseError? = null,
)

//@Serializable
//data class WalkLocationsResponse(
//    val locations: List<Location>? = null,
//    val error: ResponseError? = null,
//)
//
@Serializable
data class WalkResponse(
    @SerialName("walk") val walk: Walk? = null,
    @SerialName("error") val error: ResponseError? = null,
)

@Serializable
data class WalksResponse(
    @SerialName("walks") val walks: List<Walk>? = null,
    @SerialName("error") val error: ResponseError? = null,
)

//@Serializable
//data class WalkIdsResponse(
//    val walkIds: List<String>? = null,
//    val error: ResponseError? = null,
//)
//
//@Serializable
//data class EmptyResponse(
//    val data: Unit? = null,
//    val error: ResponseError? = null,
//)
