package hr.eduwalk.networking

import hr.eduwalk.data.model.User
import hr.eduwalk.data.sharedprefs.SharedPreferencesRepository
import hr.eduwalk.networking.model.ApiResponse
import hr.eduwalk.networking.model.toApiResponse
import javax.inject.Inject

class EduWalkRepository @Inject constructor(
    private val apiService: EduWalkApiService,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
) {
    suspend fun loginUser(user: User): ApiResponse<User?> = apiService.loginUser(user = user).toApiResponse()

    fun logoutUser() = sharedPreferencesRepository.setObject<User>(key = SharedPreferencesRepository.KEY_USER, value = null)

    fun saveUserData(user: User) = sharedPreferencesRepository.setObject(key = SharedPreferencesRepository.KEY_USER, value = user)
}
