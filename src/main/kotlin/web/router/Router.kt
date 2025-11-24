package web.router

import com.sun.net.httpserver.HttpExchange
import web.http.HttpRequest
import web.http.HttpResponse
import web.http.HttpResponseBuilder
import web.http.HttpStatus
import web.http.converter.JsonConverter

class Router(
    private val jsonConverter: JsonConverter,
    private val httpResponseBuilder: HttpResponseBuilder
) {

    private val routes = mutableListOf<Route>()
    private val pathMatcher = PathMatcher()

    /**
     * GET 메서드 라우트를 등록합니다.
     *
     * @param path 라우트 경로
     * @param handler 요청 핸들러
     */
    fun get(path: String, handler: (HttpRequest) -> HttpResponse) {
        routes.add(Route("GET", path, handler))
    }

    /**
     * POST 메서드 라우트를 등록합니다.
     *
     * @param path 라우트 경로
     * @param handler 요청 핸들러
     */
    fun post(path: String, handler: (HttpRequest) -> HttpResponse) {
        routes.add(Route("POST", path, handler))
    }

    /**
     * PUT 메서드 라우트를 등록합니다.
     *
     * @param path 라우트 경로
     * @param handler 요청 핸들러
     */
    fun put(path: String, handler: (HttpRequest) -> HttpResponse) {
        routes.add(Route("PUT", path, handler))
    }

    /**
     * DELETE 메서드 라우트를 등록합니다.
     *
     * @param path 라우트 경로
     * @param handler 요청 핸들러
     */
    fun delete(path: String, handler: (HttpRequest) -> HttpResponse) {
        routes.add(Route("DELETE", path, handler))
    }

    /**
     * HTTP 요청을 처리합니다.
     *
     * @param exchange HTTP 교환 객체
     */
    fun handle(exchange: HttpExchange) {
        val method = exchange.requestMethod
        val path = exchange.requestURI.path

        val bestMatch = findBestMatchingRoute(method, path)
        if (bestMatch == null) {
            handleNotFound(exchange)
            return
        }

        executeRoute(exchange, bestMatch)
    }

    /**
     * 요청과 매칭되는 최적의 라우트를 찾습니다.
     *
     * @param method HTTP 메서드
     * @param path 요청 경로
     * @return 매칭된 라우트 정보 또는 null
     */
    private fun findBestMatchingRoute(method: String, path: String): Triple<Route, Map<String, String>, Int>? {
        val matchedRoutes = routes.mapNotNull { route ->
            if (route.method != method) return@mapNotNull null
            val pathVariables = pathMatcher.match(route.path, path)
            if (pathVariables != null) {
                Triple(route, pathVariables, pathMatcher.getSpecificityScore(route.path))
            } else {
                null
            }
        }
        return matchedRoutes.maxByOrNull { it.third }
    }

    /**
     * 404 Not Found 응답을 처리합니다.
     *
     * @param exchange HTTP 교환 객체
     */
    private fun handleNotFound(exchange: HttpExchange) {
        val errorResponse = httpResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND)
        sendResponse(exchange, errorResponse)
    }

    /**
     * 매칭된 라우트를 실행합니다.
     *
     * @param exchange HTTP 교환 객체
     * @param match 매칭된 라우트 정보
     */
    private fun executeRoute(exchange: HttpExchange, match: Triple<Route, Map<String, String>, Int>) {
        val (route, pathVariables, _) = match
        try {
            val request = createHttpRequest(exchange, pathVariables)
            val response = route.handler(request)
            sendResponse(exchange, response)
        } catch (e: Exception) {
            handleInternalError(exchange, e)
        }
    }

    /**
     * 500 Internal Server Error 응답을 처리합니다.
     *
     * @param exchange HTTP 교환 객체
     * @param e 발생한 예외
     */
    private fun handleInternalError(exchange: HttpExchange, e: Exception) {
        val errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
        sendResponse(exchange, errorResponse)
    }

    /**
     * HttpExchange로부터 HttpRequest 객체를 생성합니다.
     *
     * @param exchange HTTP 교환 객체
     * @param pathVariables 경로 변수 맵
     * @return 생성된 HttpRequest 객체
     */
    private fun createHttpRequest(
        exchange: HttpExchange,
        pathVariables: Map<String, String>
    ): HttpRequest {
        return HttpRequest(
            method = exchange.requestMethod,
            path = exchange.requestURI.path,
            headers = exchange.requestHeaders.map { it.key to it.value.first() }.toMap(),
            body = exchange.requestBody.readBytes(),
            pathVariables = pathVariables,
            jsonConverter = jsonConverter
        )
    }

    /**
     * 성공 응답을 빌드합니다.
     *
     * @param status HTTP 상태
     * @param data 응답 데이터
     * @return 빌드된 HttpResponse 객체
     */
    fun buildSuccessResponse(status: HttpStatus, data: Any): HttpResponse {
        return httpResponseBuilder.buildSuccessResponse(status, data)
    }

    /**
     * 에러 응답을 빌드합니다.
     *
     * @param status HTTP 상태
     * @param message 에러 메시지
     * @return 빌드된 HttpResponse 객체
     */
    fun buildErrorResponse(status: HttpStatus, message: String? = null): HttpResponse {
        return httpResponseBuilder.buildErrorResponse(status, message)
    }

    /**
     * HTTP 응답을 전송합니다.
     *
     * @param exchange HTTP 교환 객체
     * @param response 전송할 HttpResponse 객체
     */
    fun sendResponse(exchange: HttpExchange, response: HttpResponse) {
        response.headers.forEach { (key, value) ->
            exchange.responseHeaders.set(key, value)
        }
        exchange.sendResponseHeaders(response.status, response.body.size.toLong())
        exchange.responseBody.use { it.write(response.body) }
    }
}
