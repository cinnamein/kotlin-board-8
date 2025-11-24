package board.config

import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory

class JpaTemplate(
    private val emf: EntityManagerFactory
) {

    /**
     * 트랜잭션 내에서 작업을 실행합니다.
     *
     * @param action 실행할 작업
     * @return 작업 실행 결과
     */
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

    /**
     * 트랜잭션 없이 작업을 실행합니다.
     *
     * @param action 실행할 작업
     * @return 작업 실행 결과
     */
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
