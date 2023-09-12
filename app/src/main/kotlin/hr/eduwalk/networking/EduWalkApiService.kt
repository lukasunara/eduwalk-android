package hr.eduwalk.networking

import hr.eduwalk.data.model.Location
import hr.eduwalk.data.model.Question
import hr.eduwalk.data.model.User
import hr.eduwalk.data.model.Walk
import hr.eduwalk.data.model.WalkScore
import hr.eduwalk.networking.model.EmptyResponse
import hr.eduwalk.networking.model.LocationQuestionsResponse
import hr.eduwalk.networking.model.LocationResponse
import hr.eduwalk.networking.model.LocationsWithScoresResponse
import hr.eduwalk.networking.model.QuestionResponse
import hr.eduwalk.networking.model.UpdateLocationScoreBody
import hr.eduwalk.networking.model.UpdateWalkRequestBody
import hr.eduwalk.networking.model.UserResponse
import hr.eduwalk.networking.model.WalkLocationsResponse
import hr.eduwalk.networking.model.WalkResponse
import hr.eduwalk.networking.model.WalkScoreTop5Response
import hr.eduwalk.networking.model.WalksResponse
import hr.eduwalk.networking.model.WalksWithScoresResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    /* --- Walk --- */
    @GET("/walk/getDefaultWalks")
    suspend fun getDefaultWalks(): WalksResponse

    @GET("/walk/getUserCreatedWalks")
    suspend fun getMyWalks(@Query("username") username: String): WalksResponse

    @GET("/join/getWalksWithScores")
    suspend fun getWalksWithScores(@Query("username") username: String): WalksWithScoresResponse

    @GET("/join/getLocationsWithScores")
    suspend fun getLocationsWithScores(@Query("walkId") walkId: String, @Query("username") username: String): LocationsWithScoresResponse

    @POST("/walk/create")
    suspend fun createWalk(@Body walk: Walk): EmptyResponse

    @DELETE("/walk/{walkId}")
    suspend fun deleteWalk(@Path("walkId") walkId: String): EmptyResponse

    @POST("/walk/{walkId}/update")
    suspend fun updateWalkInfo(@Path("walkId") walkId: String, @Body updateWalkRequestBody: UpdateWalkRequestBody): EmptyResponse

    /* --- WalkScore --- */
    @POST("/walkScore/createOrUpdate")
    suspend fun createOrUpdateWalkScore(@Body walkScore: WalkScore): EmptyResponse

    @GET("/walkScore/getTop5")
    suspend fun getTop5WalkScores(@Query("walkId") walkId: String): WalkScoreTop5Response

    /* --- Question --- */
    @GET("/question/{locationId}")
    suspend fun getLocationQuestions(@Path("locationId") locationId: Long): LocationQuestionsResponse

    @POST("/question/create")
    suspend fun createQuestion(@Body question: Question): QuestionResponse

    @POST("/question/{questionId}/update")
    suspend fun updateQuestion(@Path("questionId") questionId: Long, @Body question: Question): EmptyResponse

    @DELETE("/question/{questionId}")
    suspend fun deleteQuestion(@Path("questionId") questionId: Long): EmptyResponse

    /* --- LocationScore --- */
    @POST("/locationScore/createOrUpdate")
    suspend fun updateLocationScore(@Body locationScoreBody: UpdateLocationScoreBody): EmptyResponse

    /* --- Location --- */
    @GET("/location/{walkId}")
    suspend fun getWalkLocations(@Path("walkId") walkId: String): WalkLocationsResponse

    @POST("/location/create")
    suspend fun createLocation(@Body location: Location): LocationResponse

    @POST("/location/{locationId}/update")
    suspend fun updateLocation(@Path("locationId") locationId: Long, @Body location: Location): EmptyResponse

    @DELETE("/location/{locationId}")
    suspend fun deleteLocation(@Path("locationId") locationId: Long): EmptyResponse
}
