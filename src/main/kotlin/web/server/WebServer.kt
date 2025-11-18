package web.server

import board.config.DataSourceConfig
import board.domain.Board
import board.domain.BoardRepository
import board.infrastructure.persistence.jpa.BoardJpaRepository
import com.sun.net.httpserver.HttpServer
import di.context.ControllerScanner
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
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

        val emf: EntityManagerFactory = Persistence.createEntityManagerFactory(
            "h2-embedded",
            mapOf(
                "jakarta.persistence.nonJtaDataSource" to DataSourceConfig.hikariDataSource,
                "hibernate.hbm2ddl.auto" to "update"
            )
        )
        val boardRepository: BoardRepository = BoardJpaRepository(emf)
        val board = Board(
            title = "test title 1",
            content = "test content 1",
            author = "cinnamein"
        )
        val saved = boardRepository.save(board)

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