package web.server

import com.sun.net.httpserver.HttpExchange
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

    /**
     * 서버 생성, 라우터 설정, 라우터 등록 후 웹 서버를 시작합니다.
     */
    fun start() {
        val jsonConverter = JsonConverter()
        val httpResponseBuilder = HttpResponseBuilder(jsonConverter)
        val server = createServer()
        val router = createRouter(jsonConverter, httpResponseBuilder)
        registerRoutes(server, router)
        startServer(server)
        closeServer(server)
    }

    /**
     * HttpServer 인스턴스를 생성합니다.
     *
     * @return 설정된 포트로 바인딩된 HttpServer 인스턴스
     */
    private fun createServer(): HttpServer {
        return HttpServer.create(InetSocketAddress(port), 0)
    }

    /**
     * 라우터를 생성하고 컨트롤러를 스캔하여 등록합니다.
     *
     * @param jsonConverter JSON 변환기
     * @param httpResponseBuilder HTTP 응답 빌더
     * @return 컨트롤러가 등록된 Router 인스턴스
     */
    private fun createRouter(jsonConverter: JsonConverter, httpResponseBuilder: HttpResponseBuilder): Router {
        val router = Router(jsonConverter, httpResponseBuilder)
        val controllerScanner = ControllerScanner()
        controllerScanner.scanAndRegister(router)
        return router
    }

    /**
     * 서버에 라우트를 등록합니다.
     *
     * @param server HttpServer 인스턴스
     * @param router router 요청을 처리할 인스턴스
     */
    private fun registerRoutes(server: HttpServer, router: Router) {
        server.createContext("/") { exchange ->
            handleRequest(exchange, router)
        }
    }

    /**
     * Http 요청을 처리합니다.
     *
     * @param exchange HttpExchange 객체
     * @param router 요청을 처리할 Router 인스턴스
     */
    private fun handleRequest(exchange: HttpExchange, router: Router) {
        val path = exchange.requestURI.path
        if (path == "" || path == "/" || path == "/index.html") {
            serveIndexHtml(exchange)
        } else {
            logger.info("Incoming request: {} {}", exchange.requestMethod, path)
            router.handle(exchange)
        }
    }

    /**
     * 요청한 페이지가 메인 페이지인 경우 index.html을 제공합니다.
     *
     * @param exchange HttpExchange 객체
     */
    private fun serveIndexHtml(exchange: HttpExchange) {
        val htmlStream = javaClass.classLoader.getResourceAsStream("static/index.html")
        if (htmlStream != null) {
            sendIndexHtml(exchange, htmlStream.readAllBytes())
        } else {
            sendNotFound(exchange)
        }
    }

    /**
     * index.html 파일을 HTTP 응답으로 전송합니다.
     *
     * @param exchange HttpExchange 객체
     * @param responseBytes 전송한 html byte array
     */
    private fun sendIndexHtml(exchange: HttpExchange, responseBytes: ByteArray) {
        exchange.responseHeaders.add("Content-Type", "text/html; charset=UTF-8")
        exchange.sendResponseHeaders(200, responseBytes.size.toLong())
        exchange.responseBody.use { os ->
            os.write(responseBytes)
        }
        logger.info("Served index.html directly")
    }

    /**
     * 404 Not Found 응답을 반환합니다.
     *
     * @param exchange HttpExchange 객체
     */
    private fun sendNotFound(exchange: HttpExchange) {
        val response = "404 Not Found (Check src/main/resources/static/index.html)"
        exchange.sendResponseHeaders(404, response.length.toLong())
        exchange.responseBody.use { os ->
            os.write(response.toByteArray())
        }
    }

    /**
     * 서버를 시작합니다.
     *
     * @param server 시작할 HttpServer 인스턴스
     */
    private fun startServer(server: HttpServer) {
        server.executor = null
        server.start()
        logger.info("Server started on http://localhost:$port")
    }

    private fun closeServer(server: HttpServer) {
        Runtime.getRuntime().addShutdownHook(Thread {
            server.stop(0)
        })
    }
}
