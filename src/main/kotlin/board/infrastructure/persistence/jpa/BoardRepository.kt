package board.infrastructure.persistence.jpa

import board.config.JpaTemplate
import board.domain.Board
import di.stereotype.Repository
import jakarta.persistence.EntityManagerFactory

@Repository
class BoardRepository(
    private val emf: EntityManagerFactory
) {

    private val jpaTemplate: JpaTemplate = JpaTemplate(emf)

    /**
     * ID로 게시글을 조회합니다.
     *
     * @param id 게시글 ID
     * @return 게시글 객체 또는 null
     */
    fun findById(id: Long): Board? {
        return jpaTemplate.execute { em ->
            em.find(Board::class.java, id)
        }
    }

    /**
     * 전체 게시글을 조회합니다.
     *
     * @return 게시글 리스트
     */
    fun findAll(): List<Board> {
        return jpaTemplate.execute { em ->
            em.createQuery("SELECT b FROM Board b ORDER BY b.id DESC", Board::class.java)
                .resultList
        }
    }

    /**
     * 게시글을 저장합니다.
     *
     * @param board 저장할 게시글
     * @return 저장된 게시글
     */
    fun save(board: Board): Board {
        return jpaTemplate.executeInTransaction { em ->
            if (board.id == 0L) {
                em.persist(board)
                board
            } else {
                em.merge(board)
            }
        }
    }

    /**
     * ID로 게시글을 삭제합니다.
     *
     * @param id 삭제할 게시글 ID
     */
    fun deleteById(id: Long) {
        jpaTemplate.executeInTransaction { em ->
            val boardToRemove = em.find(Board::class.java, id)
            if (boardToRemove != null) {
                em.remove(boardToRemove)
            }
        }
    }
}