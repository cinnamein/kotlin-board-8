package board.presentation.controller

import board.domain.Board
import di.stereotype.Controller
import web.http.HttpRequest
import web.http.HttpResponse
import web.http.HttpResponseBuilder
import web.http.HttpStatus
import web.http.converter.JsonConverter
import web.method.annotation.GetMapping
import web.method.annotation.PostMapping

@Controller
class BoardController(
    private val jsonConverter: JsonConverter,
    private val httpResponseBuilder: HttpResponseBuilder = HttpResponseBuilder(jsonConverter)
) {

    @GetMapping("/boards")
    fun readBoards(): HttpResponse {
        return httpResponseBuilder.buildSuccessResponse(
            status = HttpStatus.OK,
            data = listOf(
                Board(
                    title = "test title 2",
                    content = "test content 2",
                    author = "cinnamein"
                )
            )
        )
    }

    @GetMapping("/boards/{id}")
    fun readBoard(request: HttpRequest): HttpResponse {
        val id = request.getPathVariable("id")!!
        return httpResponseBuilder.buildSuccessResponse(
            status = HttpStatus.OK,
            data = Board(
                title = "request board $id",
                content = "test content",
                author = "cinnamein"
            )
        )
    }
}