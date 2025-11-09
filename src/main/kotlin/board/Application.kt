package board

import board.config.Router
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

private val logger = LoggerFactory.getLogger("Application")

fun main() {

    val port = 8080
    val server = HttpServer.create(InetSocketAddress(port), 0)
    val router = Router()

    server.createContext("/") { exchange ->
        logger.info("Incoming request: {} {}", exchange.requestMethod, exchange.requestURI.path)
        router.handle(exchange)
    }
    server.executor = null
    server.start()
    logger.info("Server started on http://localhost:{}", port)
}
