package board.domain

interface BoardRepository {
    fun findById(id: Long): Board?
    fun findAll(): List<Board>
    fun save(board: Board): Board
    fun deleteById(id: Long)
}