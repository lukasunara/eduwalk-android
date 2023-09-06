package hr.eduwalk.networking.model

sealed class ApiResponse<out T : Any?> {
    data class Success<out T : Any?>(val data: T) : ApiResponse<T>()
    data class Error(val errorMessage: String) : ApiResponse<Nothing>()
}

fun EmptyResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = data)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

fun UserResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = user)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

fun LocationResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = location)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

fun LocationQuestionsResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = questions)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

//fun ServiceResult<LocationScore>.toLocationScoreResponse() = when (this) {
//    is ServiceResult.Success -> LocationScoreResponse(locationScore = data)
//    is ServiceResult.Error -> LocationScoreResponse(error = error)
//}
//
fun LocationsWithScoresResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = locationsWithScores)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

//fun WalkScoreResponse.toApiResponse() = when (error) {
//    null -> ApiResponse.Success(data = walkScore)
//    else -> ApiResponse.Error(errorMessage = error.toString())
//}
//
fun WalkScoreTop5Response.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = walkScores)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

fun WalkLocationsResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = locations)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

fun WalkResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = walk)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

fun WalksResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = walks)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

//fun ServiceResult<List<String>>.toWalkIdsResponse() = when (this) {
//    is ServiceResult.Success -> WalkIdsResponse(walkIds = data)
//    is ServiceResult.Error -> WalkIdsResponse(error = error)
//}
//
fun WalksWithScoresResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = walksWithScores)
    else -> ApiResponse.Error(errorMessage = error.toString())
}
