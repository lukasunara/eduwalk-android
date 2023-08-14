package hr.eduwalk.networking

import hr.eduwalk.data.model.User
import hr.eduwalk.networking.model.UserResponse
import hr.eduwalk.networking.model.WalkResponse
import hr.eduwalk.networking.model.WalksResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EduWalkApiService {

    @POST("/users/create")
    suspend fun loginUser(@Body user: User): UserResponse

    @GET("/walk/{walkId}")
    suspend fun getWalk(@Path("walkId") walkId: String): WalkResponse

    @GET("/walk/getDefaultWalks")
    suspend fun getDefaultWalks(): WalksResponse
}
