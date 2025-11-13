package board.infrastructure.persistence

import board.config.DataSourceConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.Persistence
import java.util.Properties

object ContextDI {

    val emf: EntityManagerFactory
    private val dataSource: HikariDataSource = DataSourceConfig.hikariDataSource

    init {
        val properties = Properties().apply {
            put("jakarta.persistence.nonJtaDataSource", dataSource)
            put("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            put("hibernate.hbm2ddl.auto", "create")
            put("hibernate.show_sql", "true")
            put("hibernate.format_sql", "true")
        }
        emf = Persistence.createEntityManagerFactory("h2-embedded", properties)
        Runtime.getRuntime().addShutdownHook(Thread {
            close()
        })
    }


    fun close() {
        try {
            if (emf.isOpen) emf.close()
        } catch (e: Exception) {
        }

        try {
            dataSource.close()
        } catch (e: Exception) {
        }
    }
}