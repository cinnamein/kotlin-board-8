package board

import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

private val logger = LoggerFactory.getLogger("Application")

fun main() {

    val port = 8080
    val server = HttpServer.create(InetSocketAddress(port), 0)
    server.createContext("/") { exchange ->
        val response = "Server is running on port $port"
        exchange.sendResponseHeaders(200, response.length.toLong())
        exchange.responseBody.use { it.write(response.toByteArray()) }
    }
    server.executor = null
    server.start()
    logger.info("Server started on http://localhost:{}", port)
}
