package hr.eduwalk.networking

import com.google.gson.Gson
import hr.eduwalk.data.model.User
import hr.eduwalk.data.model.Walk
import hr.eduwalk.data.sharedprefs.SharedPreferencesRepository
import hr.eduwalk.networking.model.ApiResponse
import hr.eduwalk.networking.model.toApiResponse
import javax.inject.Inject
import retrofit2.HttpException

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

    fun logoutUser() = sharedPreferencesRepository.setObject<User>(key = SharedPreferencesRepository.KEY_USER, value = null)

    fun saveUserData(user: User) = sharedPreferencesRepository.setObject(key = SharedPreferencesRepository.KEY_USER, value = user)

    /* --- Walk --- */
    suspend fun getWalk(walkId: String): ApiResponse<Walk?> = handleErrorResponse {
        apiService.getWalk(walkId = walkId)
    }.toApiResponse()

    suspend fun getDefaultWalks(): ApiResponse<List<Walk>?> = handleErrorResponse {
        apiService.getDefaultWalks()
    }.toApiResponse()
}
