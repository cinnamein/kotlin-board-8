package web.server

import com.sun.net.httpserver.HttpServer
import di.context.ControllerScanner
import org.slf4j.LoggerFactory
import web.http.HttpResponseBuilder
import web.http.converter.JsonConverter
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
        val controllerScanner = ControllerScanner()

        controllerScanner.scanAndRegister(router)
        server.createContext("/") { exchange ->
            logger.info("Incoming request: {} {}", exchange.requestMethod, exchange.requestURI.path)
            router.handle(exchange)
        }
        server.executor = null
        server.start()
        logger.info("Server started on http://localhost:$port")
    }
}