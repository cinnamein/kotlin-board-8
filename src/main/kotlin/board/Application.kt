package board

import board.config.HttpResponseBuilder
import board.config.HttpStatus
import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

private val logger = LoggerFactory.getLogger("Application")

fun main() {

    val port = 8080
    val objectMapper = ObjectMapper()
    val responseBuilder = HttpResponseBuilder(objectMapper)
    val server = HttpServer.create(InetSocketAddress(port), 0)
    server.createContext("/") { exchange ->
        val responseData = mapOf(
            "message" to "Server is running on port $port",
            "status" to "OK"
        )
        val finalResponse = responseBuilder.buildSuccessResponse(
            status = HttpStatus.OK,
            data = responseData
        )

        finalResponse.headers.forEach { (key, value) ->
            exchange.responseHeaders.set(key, value)
        }
        exchange.sendResponseHeaders(finalResponse.status, finalResponse.body.size.toLong())
        exchange.responseBody.use { it.write(finalResponse.body) }
    }
    server.executor = null
    server.start()
    logger.info("Server started on http://localhost:{}", port)
}
