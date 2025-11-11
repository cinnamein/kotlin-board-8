package board.presentation

class HttpResponseBuilder(
    private val jsonConverter: JsonConverter
) {

    fun buildSuccessResponse(
        status: HttpStatus,
        data: Any,
    ): HttpResponse {
        val jsonBody = jsonConverter.serialize(data)
        return HttpResponse(
            status = status.statusCode,
            headers = mapOf("Content-Type" to "application/json"),
            body = jsonBody,
        )
    }

    fun buildErrorResponse(
        status: HttpStatus,
        message: String? = null,
    ): HttpResponse {
        val finalMessage = message ?: status.message
        val errorDto = ErrorResponse(
            errorCode = status.statusCode,
            message = finalMessage
        )
        val jsonBody = jsonConverter.serialize(errorDto)
        return HttpResponse(
            status = status.statusCode,
            headers = mapOf("Content-Type" to "application/json"),
            body = jsonBody,
        )
    }
}