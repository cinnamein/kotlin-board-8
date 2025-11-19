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

    fun findById(id: Long): Board? {
        return jpaTemplate.execute { em ->
            em.find(Board::class.java, id)
        }
    }

    fun findAll(): List<Board> {
        return jpaTemplate.execute { em ->
            em.createQuery("SELECT b FROM Board b ORDER BY b.id DESC", Board::class.java)
                .resultList
        }
    }

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

    fun deleteById(id: Long) {
        jpaTemplate.executeInTransaction { em ->
            val boardToRemove = em.find(Board::class.java, id)
            if (boardToRemove != null) {
                em.remove(boardToRemove)
            }
        }
    }
}