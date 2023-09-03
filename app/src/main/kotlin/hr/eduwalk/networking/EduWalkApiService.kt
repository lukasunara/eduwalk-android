package hr.eduwalk.networking

import hr.eduwalk.data.model.User
import hr.eduwalk.data.model.WalkScore
import hr.eduwalk.networking.model.EmptyResponse
import hr.eduwalk.networking.model.LocationQuestionsResponse
import hr.eduwalk.networking.model.LocationsWithScoresResponse
import hr.eduwalk.networking.model.UpdateLocationScoreBody
import hr.eduwalk.networking.model.UserResponse
import hr.eduwalk.networking.model.WalkResponse
import hr.eduwalk.networking.model.WalkScoreTop5Response
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

    /* --- WalkScore --- */
    @POST("/walkScore/createOrUpdate")
    suspend fun createOrUpdateWalkScore(@Body walkScore: WalkScore): EmptyResponse

    @GET("/walkScore/getTop5")
    suspend fun getTop5WalkScores(@Query("walkId") walkId: String): WalkScoreTop5Response

    /* --- Question --- */
    @GET("/question/{locationId}")
    suspend fun getLocationQuestions(@Path("locationId") locationId: Int): LocationQuestionsResponse

    /* --- LocationScore --- */
    @POST("/locationScore/createOrUpdate")
    suspend fun updateLocationScore(@Body locationScoreBody: UpdateLocationScoreBody): EmptyResponse
}
