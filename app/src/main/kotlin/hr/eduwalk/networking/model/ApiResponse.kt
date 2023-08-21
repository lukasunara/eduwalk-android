package hr.eduwalk.networking.model

sealed class ApiResponse<out T : Any?> {
    data class Success<out T : Any?>(val data: T) : ApiResponse<T>()
    data class Error(val errorMessage: String) : ApiResponse<Nothing>()
}

fun UserResponse.toApiResponse() = when (error) {
    null -> ApiResponse.Success(data = user)
    else -> ApiResponse.Error(errorMessage = error.toString())
}

//fun ServiceResult<List<Question>>.toLocationQuestionsResponse() = when (this) {
//    is ServiceResult.Success -> LocationQuestionsResponse(questions = data)
//    is ServiceResult.Error -> LocationQuestionsResponse(error = error)
//}
//
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

//fun ServiceResult<List<Location>>.toWalkLocationsResponse() = when (this) {
//    is ServiceResult.Success -> WalkLocationsResponse(locations = data)
//    is ServiceResult.Error -> WalkLocationsResponse(error = error)
//}
//
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
//fun ServiceResult<Unit>.toEmptyResponse() = when (this) {
//    is ServiceResult.Success -> EmptyResponse(data = data)
//    is ServiceResult.Error -> EmptyResponse(error = error)
//}
