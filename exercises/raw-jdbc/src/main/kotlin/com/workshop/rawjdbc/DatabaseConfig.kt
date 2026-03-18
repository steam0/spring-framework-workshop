package com.workshop.rawjdbc

import org.h2.jdbcx.JdbcDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DatabaseConfig {

    @Bean
    fun dataSource(): DataSource {
        val ds = JdbcDataSource()
        ds.setURL("jdbc:h2:mem:testdb")
        return ds
    }
}
