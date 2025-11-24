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

    /**
     * 등록된 전체 게시글을 조회합니다.
     *
     * @return 게시글 리스트
     */
    fun getAllBoards(): List<Board> {
        return boardRepository.findAll()
    }

    /**
     * 게시글을 생성합니다.
     *
     * @param board 생성할 게시글 정보
     * @return 생성된 게시글 응답 DTO
     */
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

    /**
     * 특정 게시글을 조회합니다.
     *
     * @param boardId 게시글 ID
     * @return 게시글 객체
     */
    fun getBoard(boardId: Long): Board {
        return boardRepository.findById(boardId) ?: throw NoSuchElementException("Board not found")
    }

    /**
     * 게시글을 수정합니다.
     *
     * @param boardUpdateDto 수정할 게시글 정보
     * @return 수정된 게시글 응답 DTO
     */
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

    /**
     * 게시글을 삭제합니다.
     *
     * @param boardId 삭제할 게시글 ID
     */
    fun deleteBoard(boardId: Long) {
        return boardRepository.deleteById(boardId)
    }
}