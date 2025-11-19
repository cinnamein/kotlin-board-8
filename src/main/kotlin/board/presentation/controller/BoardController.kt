package board.presentation.controller

import board.application.service.BoardService
import board.presentation.dto.BoardRequestDto
import board.presentation.dto.BoardUpdateRequestDto
import di.stereotype.Controller
import web.http.HttpRequest
import web.http.HttpResponse
import web.http.HttpResponseBuilder
import web.http.HttpStatus
import web.http.converter.JsonConverter
import web.http.readBody
import web.method.annotation.DeleteMapping
import web.method.annotation.GetMapping
import web.method.annotation.PostMapping
import web.method.annotation.PutMapping

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

    @PostMapping("/boards")
    fun createBoard(request: HttpRequest): HttpResponse {
        val boardRequest = request.readBody<BoardRequestDto>()
        val createdBoard = boardService.createBoard(boardRequest)
        return httpResponseBuilder.buildSuccessResponse(
            status = HttpStatus.CREATED,
            data = createdBoard
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

    @PutMapping("/boards")
    fun updateBoard(request: HttpRequest): HttpResponse {
        val boardUpdateRequest = request.readBody<BoardUpdateRequestDto>()
        val board = boardService.updateBoard(boardUpdateRequest)
        return httpResponseBuilder.buildSuccessResponse(
            status = HttpStatus.OK,
            data = board
        )
    }

    @DeleteMapping("/boards/{id}")
    fun deleteBoard(request: HttpRequest): HttpResponse {
        val id = request.getPathVariable("id")!!
        val board = boardService.deleteBoard(id.toLong())
        return httpResponseBuilder.buildSuccessResponse(
            status = HttpStatus.OK,
            data = board
        )
    }
}