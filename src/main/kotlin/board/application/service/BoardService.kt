package board.application.service

import board.domain.Board
import board.infrastructure.persistence.jpa.BoardRepository
import board.presentation.dto.BoardRequestDto
import board.presentation.dto.BoardResponseDto
import board.presentation.dto.BoardUpdateRequestDto
import board.presentation.dto.BoardUpdateResponseDto
import di.stereotype.Service

@Service
class BoardService(
    private val boardRepository: BoardRepository
) {

    fun getAllBoards(): List<Board> {
        return boardRepository.findAll()
    }

    fun createBoard(board: BoardRequestDto): BoardResponseDto {
        val board = boardRepository.save(
            Board(
                title = board.title,
                content = board.content,
                author = board.author,
            )
        )
        return BoardResponseDto(
            id = board.id,
            title = board.title,
            content = board.content,
            author = board.author,
            createdAt = board.createdAt.toString(),
        )
    }

    fun getBoard(boardId: Long): Board {
        return boardRepository.findById(boardId) ?: throw NoSuchElementException("Board not found")
    }

    fun updateBoard(boardUpdateDto: BoardUpdateRequestDto): BoardUpdateResponseDto {
        val board = boardRepository.findById(boardUpdateDto.id) ?: throw NoSuchElementException("Board not found")
        val newBoard = boardRepository.save(
            Board(
                id = boardUpdateDto.id,
                title = boardUpdateDto.title,
                content = boardUpdateDto.content,
                board.author,
            )
        )
        return BoardUpdateResponseDto(
            id = newBoard.id,
            title = newBoard.title,
            content = newBoard.content
        )
    }

    fun deleteBoard(boardId: Long) {
        return boardRepository.deleteById(boardId)
    }
}