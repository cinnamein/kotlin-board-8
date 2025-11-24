package board.config

import di.stereotype.Bean
import di.stereotype.Configuration
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence

@Configuration
class EntityManagerConfig {

    /**
     * EntityManagerFactory 빈을 생성합니다.
     *
     * @return EntityManagerFactory 인스턴스
     */
    @Bean
    fun entityManagerFactory(): EntityManagerFactory {
        return Persistence.createEntityManagerFactory(
            "h2-embedded",
            mapOf(
                "jakarta.persistence.nonJtaDataSource" to DataSourceConfig.hikariDataSource,
                "hibernate.hbm2ddl.auto" to "update"
            )
        )
    }
}