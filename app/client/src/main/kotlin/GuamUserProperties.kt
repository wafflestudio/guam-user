package waffle.guam.user.client

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("guam.user")
@ConstructorBinding
data class GuamUserProperties(
    val url: String? = null,
    val fallback: Boolean = false,
)
