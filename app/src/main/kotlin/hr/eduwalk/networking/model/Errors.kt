package hr.eduwalk.networking.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseError(
//    val code: ErrorCode,
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
)
//{
//    constructor(errorCode: ErrorCode) : this(code = errorCode, message = errorCode.message)
//}
//
//enum class ErrorCode(val message: String) {
//
//    UNKNOWN_LOCATION("This location doesn't exist."),
//    UNKNOWN_LOCATION_SCORE("This location has no score for given user."),
//    UNKNOWN_QUESTION("This question doesn't exist."),
//    UNKNOWN_USER("This user doesn't exist."),
//    UNKNOWN_WALK("This walk doesn't exist."),
//    UNKNOWN_WALK_SCORE("This walk has no score for given user."),
//
//    LOCATION_EXISTS("This location already exists."),
//    LOCATION_SCORE_EXISTS("This location score already exists."),
//    QUESTION_EXISTS("This question already exists."),
//    USER_EXISTS("This user already exists."),
//    WALK_EXISTS("This walk already exists."),
//    WALK_SCORE_EXISTS("This walk score already exists."),
//
//    UPDATE_WALK_FAILED("Update walk failed."),
//
//    DATABASE_ERROR("Unknown database error. Try again, and check your parameters."),
//    INVALID_JSON("Your JSON must match the format in this sample response."),
//}
