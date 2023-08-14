package hr.eduwalk.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("username") val username: String,
    @SerialName("role") val role: UserRole = UserRole.STUDENT,
)

enum class UserRole {
    STUDENT, TEACHER
}
