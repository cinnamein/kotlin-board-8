package board.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpExchange

class Router {

    private val routes = mutableListOf<Route>()
    private val objectMapper = ObjectMapper()
    private val httpResponseBuilder = HttpResponseBuilder(objectMapper)

    fun get(path: String, handler: (HttpExchange) -> Unit) {
        routes.add(Route("GET", path, handler))
    }

    fun post(path: String, handler: (HttpExchange) -> Unit) {
        routes.add(Route("POST", path, handler))
    }

    fun put(path: String, handler: (HttpExchange) -> Unit) {
        routes.add(Route("PUT", path, handler))
    }

    fun delete(path: String, handler: (HttpExchange) -> Unit) {
        routes.add(Route("DELETE", path, handler))
    }

    fun handle(exchange: HttpExchange) {
        val method = exchange.requestMethod
        val path = exchange.requestURI.path

        val route = routes.find { it.method == method && it.path == path }
        if (route == null) {
            val errorResponse = httpResponseBuilder.buildErrorResponse(HttpStatus.NOT_FOUND)
            sendResponse(exchange, errorResponse)
            return
        }

        try {
            route.handler(exchange)
        } catch (e: Exception) {
            val errorResponse = httpResponseBuilder.buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.message
            )
            sendResponse(exchange, errorResponse)
        }
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
