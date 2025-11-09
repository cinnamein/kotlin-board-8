package board.config

import com.fasterxml.jackson.databind.ObjectMapper

class HttpResponseBuilder(
    private val objectMapper: ObjectMapper,
) {

    fun buildSuccessResponse(
        status: HttpStatus,
        data: Any,
    ): HttpResponse {
        val jsonBody = objectMapper.writeValueAsBytes(data)
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
        val jsonBody = objectMapper.writeValueAsBytes(errorDto)
        return HttpResponse(
            status = status.statusCode,
            headers = mapOf("Content-Type" to "application/json"),
            body = jsonBody,
        )
    }
}