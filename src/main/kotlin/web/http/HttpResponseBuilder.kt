package web.http

import web.http.converter.JsonConverter

class HttpResponseBuilder(
    private val jsonConverter: JsonConverter
) {

    /**
     * 성공 응답을 빌드합니다.
     *
     * @param status HTTP 상태
     * @param data 응답 데이터
     * @return HTTP 응답 객체
     */
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

    /**
     * 에러 응답을 빌드합니다.
     *
     * @param status HTTP 상태
     * @param message 에러 메시지
     * @return HTTP 응답 객체
     */
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