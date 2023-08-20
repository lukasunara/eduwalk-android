package hr.eduwalk.networking

import hr.eduwalk.data.model.User
import hr.eduwalk.networking.model.LocationsWithScoresResponse
import hr.eduwalk.networking.model.UserResponse
import hr.eduwalk.networking.model.WalkResponse
import hr.eduwalk.networking.model.WalksResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EduWalkApiService {

    @POST("/users/create")
    suspend fun loginUser(@Body user: User): UserResponse

    /* --- User --- */
    @GET("/walk/{walkId}")
    suspend fun getWalk(@Path("walkId") walkId: String): WalkResponse

    @GET("/walk/getDefaultWalks")
    suspend fun getDefaultWalks(): WalksResponse

    /* --- Walk --- */
    @GET("join/getLocationsWithScores")
    suspend fun getLocationsWithScores(@Query("walkId") walkId: String, @Query("username") username: String): LocationsWithScoresResponse
}
