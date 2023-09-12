package hr.eduwalk.networking

import com.google.gson.Gson
import hr.eduwalk.data.model.Location
import hr.eduwalk.data.model.LocationWithScore
import hr.eduwalk.data.model.Question
import hr.eduwalk.data.model.User
import hr.eduwalk.data.model.Walk
import hr.eduwalk.data.model.WalkScore
import hr.eduwalk.data.model.WalkWithScore
import hr.eduwalk.data.sharedprefs.SharedPreferencesRepository
import hr.eduwalk.networking.model.ApiResponse
import hr.eduwalk.networking.model.UpdateLocationScoreBody
import hr.eduwalk.networking.model.UpdateWalkRequestBody
import hr.eduwalk.networking.model.toApiResponse
import javax.inject.Inject
import retrofit2.HttpException
import retrofit2.http.Path

class EduWalkRepository @Inject constructor(
    private val apiService: EduWalkApiService,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
) {
    private inline fun <reified T> handleErrorResponse(apiCall: () -> T) = try {
        apiCall()
    } catch (e: HttpException) {
        val jsonResponse = e.response()?.errorBody()?.string().orEmpty()
        Gson().fromJson(jsonResponse, T::class.java)
    }

    /* --- User --- */
    suspend fun loginUser(user: User): ApiResponse<User?> = handleErrorResponse {
        apiService.loginUser(user = user)
    }.toApiResponse()

    fun getUser() = sharedPreferencesRepository.getObject<User>(key = SharedPreferencesRepository.KEY_USER)

    fun logoutUser() = sharedPreferencesRepository.setObject<User>(key = SharedPreferencesRepository.KEY_USER, value = null)

    fun saveUserData(user: User) = sharedPreferencesRepository.setObject(key = SharedPreferencesRepository.KEY_USER, value = user)

    suspend fun getWalksWithScores(): ApiResponse<List<WalkWithScore>?> = handleErrorResponse {
        apiService.getWalksWithScores(username = getUser()!!.username)
    }.toApiResponse()

    /* --- Walk --- */
    suspend fun getWalk(walkId: String): ApiResponse<Walk?> = handleErrorResponse {
        apiService.getWalk(walkId = walkId)
    }.toApiResponse()

    suspend fun createWalk(walk: Walk): ApiResponse<Unit?> = handleErrorResponse {
        apiService.createWalk(walk = walk)
    }.toApiResponse()

    suspend fun deleteWalk(@Path("walkId") walkId: String): ApiResponse<Unit?> = handleErrorResponse {
        apiService.deleteWalk(walkId = walkId)
    }.toApiResponse()

    suspend fun updateWalkInfo(walkId: String, walkTitle: String, walkDescription: String?): ApiResponse<Unit?> = handleErrorResponse {
        apiService.updateWalkInfo(
            walkId = walkId,
            updateWalkRequestBody = UpdateWalkRequestBody(
                title = walkTitle,
                description = walkDescription,
            )
        )
    }.toApiResponse()

    suspend fun getDefaultWalks(): ApiResponse<List<Walk>?> = handleErrorResponse {
        apiService.getDefaultWalks()
    }.toApiResponse()

    suspend fun getMyWalks(): ApiResponse<List<Walk>?> = handleErrorResponse {
        apiService.getMyWalks(username = getUser()!!.username)
    }.toApiResponse()

    /* --- Location --- */
    suspend fun getLocationsWithScores(walkId: String): ApiResponse<List<LocationWithScore>?> = handleErrorResponse {
        apiService.getLocationsWithScores(walkId = walkId, username = getUser()!!.username)
    }.toApiResponse()

    suspend fun getWalkLocations(walkId: String): ApiResponse<List<Location>?> = handleErrorResponse {
        apiService.getWalkLocations(walkId = walkId)
    }.toApiResponse()

    suspend fun createLocation(location: Location): ApiResponse<Location?> = handleErrorResponse {
        apiService.createLocation(location = location)
    }.toApiResponse()

    suspend fun updateLocation(location: Location): ApiResponse<Unit?> = handleErrorResponse {
        apiService.updateLocation(locationId = location.id, location = location)
    }.toApiResponse()

    suspend fun deleteLocation(locationId: Long): ApiResponse<Unit?> = handleErrorResponse {
        apiService.deleteLocation(locationId = locationId)
    }.toApiResponse()

    /* --- WalkScore --- */
    suspend fun createOrUpdateWalkScore(walkId: String, newScore: Int): ApiResponse<Unit?> = handleErrorResponse {
        apiService.createOrUpdateWalkScore(walkScore = WalkScore(username = getUser()!!.username, walkId = walkId, score = newScore))
    }.toApiResponse()

    suspend fun getTop5WalkScores(walkId: String): ApiResponse<List<WalkScore>?> = handleErrorResponse {
        apiService.getTop5WalkScores(walkId = walkId)
    }.toApiResponse()

    /* --- Question --- */
    suspend fun getLocationQuestions(locationId: Long): ApiResponse<List<Question>?> = handleErrorResponse {
        apiService.getLocationQuestions(locationId = locationId)
    }.toApiResponse()

    suspend fun createQuestion(question: Question): ApiResponse<Question?> = handleErrorResponse {
        apiService.createQuestion(question = question)
    }.toApiResponse()

    suspend fun updateQuestion(question: Question): ApiResponse<Unit?> = handleErrorResponse {
        apiService.updateQuestion(questionId = question.id, question = question)
    }.toApiResponse()

    suspend fun deleteQuestion(questionId: Long): ApiResponse<Unit?> = handleErrorResponse {
        apiService.deleteQuestion(questionId = questionId)
    }.toApiResponse()

    /* --- LocationScore --- */
    suspend fun updateLocationScore(locationId: Long, newBestScore: Int): ApiResponse<Unit?> = handleErrorResponse {
        apiService.updateLocationScore(
            locationScoreBody = UpdateLocationScoreBody(
                userId = getUser()!!.username,
                locationId = locationId,
                score = newBestScore,
            )
        )
    }.toApiResponse()

    companion object {
        const val NUMBER_OF_QUESTIONS_PER_QUIZ = 3
    }
}
