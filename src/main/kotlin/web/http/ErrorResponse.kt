package web.http

data class ErrorResponse(
    val errorCode: Int,
    val message: String,
)
