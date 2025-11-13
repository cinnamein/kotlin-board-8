package board.config

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory

class JpaTemplate(
    private val emf: EntityManagerFactory
) {

    fun <T> executeInTransaction(
        action: (EntityManager) -> T
    ): T {
        val em = emf.createEntityManager()
        val tx = em.transaction
        try {
            tx.begin()
            val result = action(em)
            tx.commit()
            return result
        } catch (e: Exception) {
            if (tx.isActive) {
                tx.rollback()
            }
            throw RuntimeException("Transaction failed", e)
        } finally {
            em.close()
        }
    }

    fun <T> execute(
        action: (EntityManager) -> T
    ): T {
        val em = emf.createEntityManager()
        try {
            return action(em)
        } finally {
            em.close()
        }
    }
}