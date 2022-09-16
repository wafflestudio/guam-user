package waffle.guam.user.client

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.web.reactive.function.client.WebClient
import waffle.guam.user.client.impl.GuamUserBlockingClientImpl
import waffle.guam.user.client.impl.GuamUserClientImpl

@EnableConfigurationProperties(GuamUserProperties::class)
@Configuration
class GuamUserAutoConfiguration {

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean(GuamUserClient.Blocking::class)
    @Bean
    fun blockingClient(
        properties: GuamUserProperties,
        builder: WebClient.Builder,
        env: Environment,
    ): GuamUserClient.Blocking {
        return GuamUserBlockingClientImpl(
            url = properties.url ?: url(env),
            builder = builder,
            fallback = properties.fallback
        )
    }

    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    @ConditionalOnMissingBean(GuamUserClient::class)
    @Bean
    fun client(
        properties: GuamUserProperties,
        builder: WebClient.Builder,
        env: Environment,
    ): GuamUserClient {
        return GuamUserClientImpl(
            url = properties.url ?: url(env),
            builder = builder,
            fallback = properties.fallback
        )
    }

    fun url(env: Environment) = when {
        env.acceptsProfiles(Profiles.of("dev")) -> "http://guam-user.jon-snow-korea.com"
        else -> TODO()
    }
}
