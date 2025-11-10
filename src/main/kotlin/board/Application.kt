package board

import board.config.HttpResponseBuilder
import board.config.HttpStatus
import board.config.JsonConverter
import board.config.Router
import board.domain.Board
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

private val logger = LoggerFactory.getLogger("Application")

fun main() {

    val jsonConverter = JsonConverter()
    val httpResponseBuilder = HttpResponseBuilder(jsonConverter)

    val port = 8080
    val server = HttpServer.create(InetSocketAddress(port), 0)
    val router = Router(jsonConverter, httpResponseBuilder)

    server.createContext("/") { exchange ->
        logger.info("Incoming request: {} {}", exchange.requestMethod, exchange.requestURI.path)
        router.handle(exchange)
    }
    server.executor = null
    server.start()
    logger.info("Server started on http://localhost:{}", port)
}
