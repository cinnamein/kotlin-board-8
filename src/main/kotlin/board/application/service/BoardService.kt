package board.application.service

import board.domain.Board
import board.infrastructure.persistence.jpa.BoardRepository
import di.stereotype.Service

@Service
class BoardService(
    private val boardRepository: BoardRepository
) {

    fun getAllBoards(): List<Board> {
        return boardRepository.findAll()
    }

    fun getBoard(boardId: Long): Board {
        return boardRepository.findById(boardId) ?: throw NoSuchElementException("Board not found")
    }
}