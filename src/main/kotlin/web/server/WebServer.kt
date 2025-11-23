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
            val path = exchange.requestURI.path
            if (path == "" || path == "/" || path == "/index.html") {
                val htmlStream = javaClass.classLoader.getResourceAsStream("static/index.html")

                if (htmlStream != null) {
                    val responseBytes = htmlStream.readAllBytes()
                    exchange.responseHeaders.add("Content-Type", "text/html; charset=UTF-8")
                    exchange.sendResponseHeaders(200, responseBytes.size.toLong())
                    exchange.responseBody.use { os ->
                        os.write(responseBytes)
                    }
                    logger.info("Served index.html directly")
                } else {
                    val response = "404 Not Found (Check src/main/resources/static/index.html)"
                    exchange.sendResponseHeaders(404, response.length.toLong())
                    exchange.responseBody.use { os ->
                        os.write(response.toByteArray())
                    }
                }
            } else {
                logger.info("Incoming request: {} {}", exchange.requestMethod, path)
                router.handle(exchange)
            }
        }
        server.executor = null
        server.start()
        logger.info("Server started on http://localhost:$port")
    }
}