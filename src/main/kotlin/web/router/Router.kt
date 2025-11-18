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

    fun get(path: String, handler: (HttpRequest) -> HttpResponse) {
        routes.add(Route("GET", path, handler))
    }

    fun post(path: String, handler: (HttpRequest) -> HttpResponse) {
        routes.add(Route("POST", path, handler))
    }

    fun put(path: String, handler: (HttpRequest) -> HttpResponse) {
        routes.add(Route("PUT", path, handler))
    }

    fun delete(path: String, handler: (HttpRequest) -> HttpResponse) {
        routes.add(Route("DELETE", path, handler))
    }

    fun handle(exchange: HttpExchange) {
        val method = exchange.requestMethod
        val path = exchange.requestURI.path

        val matchedRoutes = routes.mapNotNull { route ->
            if (route.method != method) {
                return@mapNotNull null
            }
            val pathVariables = pathMatcher.match(route.path, path)
            if (pathVariables != null) {
                Triple(route, pathVariables, pathMatcher.getSpecificityScore(route.path))
            } else {
                null
            }
        }

        val bestMatch = matchedRoutes.maxByOrNull { it.third }
        if (bestMatch == null) {
            val errorResponse = httpResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND)
            sendResponse(exchange, errorResponse)
            return
        }
        val (route, pathVariables, _) = bestMatch

        try {
            val request = createHttpRequest(exchange, pathVariables)
            val response = route.handler(request)
            sendResponse(exchange, response)
        } catch (e: Exception) {
            val errorResponse = buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.message)
            sendResponse(exchange, errorResponse)
        }
    }

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

    fun buildSuccessResponse(status: HttpStatus, data: Any): HttpResponse {
        return httpResponseBuilder.buildSuccessResponse(status, data)
    }

    fun buildErrorResponse(status: HttpStatus, message: String? = null): HttpResponse {
        return httpResponseBuilder.buildErrorResponse(status, message)
    }

    fun sendResponse(exchange: HttpExchange, response: HttpResponse) {
        response.headers.forEach { (key, value) ->
            exchange.responseHeaders.set(key, value)
        }
        exchange.sendResponseHeaders(response.status, response.body.size.toLong())
        exchange.responseBody.use { it.write(response.body) }
    }
}
