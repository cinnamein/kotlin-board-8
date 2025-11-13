package board.infrastructure.persistence.jpa

import board.config.JpaTemplate
import board.domain.Board
import board.domain.BoardRepository
import jakarta.persistence.EntityManagerFactory

class BoardJpaRepository(
    emf: EntityManagerFactory
) : BoardRepository {

    private val jpaTemplate: JpaTemplate = JpaTemplate(emf)

    override fun findById(id: Long): Board? {
        return jpaTemplate.execute { em ->
            em.find(Board::class.java, id)
        }
    }

    override fun findAll(): List<Board> {
        return jpaTemplate.execute { em ->
            em.createQuery("SELECT b FROM Board b ORDER BY b.id DESC", Board::class.java)
                .resultList
        }
    }

    override fun save(board: Board): Board {
        return jpaTemplate.executeInTransaction { em ->
            if (board.id == 0L) {
                em.persist(board)
                board
            } else {
                em.merge(board)
            }
        }
    }

    override fun deleteById(id: Long) {
        jpaTemplate.executeInTransaction { em ->
            val boardToRemove = em.find(Board::class.java, id)
            if (boardToRemove != null) {
                em.remove(boardToRemove)
            }
        }
    }
}