package no.norsktipping.boot.fenris

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "nt.fenris")
class FenrisProperties {
    var url: String? = null
    var username: String? = null
    var password: String? = null
    var pool: Pool = Pool()

    class Pool {
        var maxSize: Int = 10
        var minIdle: Int = 2
        var connectionTimeout: Duration = Duration.ofSeconds(3)
    }
}
