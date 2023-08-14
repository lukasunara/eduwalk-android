package hr.eduwalk.networking

import hr.eduwalk.data.model.User
import hr.eduwalk.networking.model.ApiResponse
import hr.eduwalk.networking.model.toApiResponse
import javax.inject.Inject

class EduWalkRepository @Inject constructor(
    private val apiService: EduWalkApiService,
) {
    suspend fun login(user: User): ApiResponse<User?> = apiService.login(user = user).toApiResponse()
}
