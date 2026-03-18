package no.norsktipping.boot.fenris

import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import javax.sql.DataSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FenrisAutoConfigurationTest {

    private val runner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(FenrisAutoConfiguration::class.java))

    @Test
    fun `creates DataSource when nt_fenris_url is set`() {
        runner.withPropertyValues(
            "nt.fenris.url=jdbc:h2:mem:testdb",
            "nt.fenris.username=sa"
        ).run { context ->
            assertNotNull(context.getBean(DataSource::class.java))
            val ds = context.getBean(HikariDataSource::class.java)
            assertEquals("jdbc:h2:mem:testdb", ds.jdbcUrl)
            assertEquals("sa", ds.username)
        }
    }

    @Test
    fun `applies pool settings`() {
        runner.withPropertyValues(
            "nt.fenris.url=jdbc:h2:mem:testdb",
            "nt.fenris.pool.max-size=20",
            "nt.fenris.pool.min-idle=5",
            "nt.fenris.pool.connection-timeout=10s"
        ).run { context ->
            val ds = context.getBean(HikariDataSource::class.java)
            assertEquals(20, ds.maximumPoolSize)
            assertEquals(5, ds.minimumIdle)
            assertEquals(10_000, ds.connectionTimeout)
        }
    }

    @Test
    fun `uses defaults when pool settings not specified`() {
        runner.withPropertyValues(
            "nt.fenris.url=jdbc:h2:mem:testdb"
        ).run { context ->
            val ds = context.getBean(HikariDataSource::class.java)
            assertEquals(10, ds.maximumPoolSize)
            assertEquals(2, ds.minimumIdle)
            assertEquals(3_000, ds.connectionTimeout)
        }
    }

    @Test
    fun `does not create DataSource when url is missing`() {
        runner.run { context ->
            assertNull(context.getBean(DataSource::class.java, null))
        }
    }

    @Test
    fun `backs off when DataSource already exists`() {
        runner.withPropertyValues(
            "nt.fenris.url=jdbc:h2:mem:testdb"
        ).withBean(DataSource::class.java, {
            HikariDataSource().apply { jdbcUrl = "jdbc:h2:mem:existingdb" }
        }).run { context ->
            val ds = context.getBean(HikariDataSource::class.java)
            assertEquals("jdbc:h2:mem:existingdb", ds.jdbcUrl)
        }
    }
}
