package web.server

import web.http.converter.JsonConverter
import com.sun.net.httpserver.HttpServer
import org.slf4j.LoggerFactory
import web.http.HttpResponseBuilder
import web.router.Router
import java.net.InetSocketAddress

class WebServer(
    private val port: Int = 8080,
) {

    private val logger = LoggerFactory.getLogger(WebServer::class.java)

    fun start() {
        val jsonConverter = JsonConverter()
        val httpResponseBuilder = HttpResponseBuilder(jsonConverter)
        val server = HttpServer.create(InetSocketAddress(port), 0)
        val router = Router(jsonConverter, httpResponseBuilder)

        server.createContext("/") { exchange ->
            logger.info("Incoming request: {} {}", exchange.requestMethod, exchange.requestURI.path)
            router.handle(exchange)
        }
        server.executor = null
        server.start()
        logger.info("Server started on http://localhost:$port")
    }
}