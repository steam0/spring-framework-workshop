package no.norsktipping.boot.fenris

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
@ConditionalOnProperty(name = ["nt.fenris.url"])
@EnableConfigurationProperties(FenrisProperties::class)
class FenrisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DataSource::class)
    fun dataSource(properties: FenrisProperties): HikariDataSource {
        val ds = HikariDataSource()
        ds.jdbcUrl = properties.url
        ds.username = properties.username
        ds.password = properties.password
        ds.maximumPoolSize = properties.pool.maxSize
        ds.minimumIdle = properties.pool.minIdle
        ds.connectionTimeout = properties.pool.connectionTimeout.toMillis()
        return ds
    }
}
