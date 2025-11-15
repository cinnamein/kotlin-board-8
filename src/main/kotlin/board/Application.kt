package board

import board.infrastructure.di.context.BeanFactory
import board.presentation.HttpResponseBuilder
import board.presentation.JsonConverter
import board.presentation.Router
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import kotlin.jvm.java

private val logger = LoggerFactory.getLogger("Application")

class Application

fun main() {

    BeanFactory.init(Application::class.java)

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
