package board.presentation.controller

import board.application.service.BoardService
import di.stereotype.Controller
import web.http.HttpRequest
import web.http.HttpResponse
import web.http.HttpResponseBuilder
import web.http.HttpStatus
import web.http.converter.JsonConverter
import web.method.annotation.GetMapping

@Controller
class BoardController(
    private val boardService: BoardService,
    private val jsonConverter: JsonConverter,
    private val httpResponseBuilder: HttpResponseBuilder = HttpResponseBuilder(jsonConverter)
) {

    @GetMapping("/boards")
    fun readBoards(): HttpResponse {
        val boards = boardService.getAllBoards()
        return httpResponseBuilder.buildSuccessResponse(
            status = HttpStatus.OK,
            data = boards
        )
    }

    @GetMapping("/boards/{id}")
    fun readBoard(request: HttpRequest): HttpResponse {
        val id = request.getPathVariable("id")!!
        val board = boardService.getBoard(id.toLong())
        return httpResponseBuilder.buildSuccessResponse(
            status = HttpStatus.OK,
            data = board
        )
    }
}