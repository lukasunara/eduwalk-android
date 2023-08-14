package hr.eduwalk.networking

import hr.eduwalk.data.model.User
import hr.eduwalk.networking.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface EduWalkApiService {

    @POST("/users/create")
    suspend fun loginUser(@Body user: User): UserResponse
}
