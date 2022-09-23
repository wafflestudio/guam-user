package waffle.guam.user.api.gateway

import kotlinx.coroutines.reactor.mono
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import waffle.guam.user.domain.AuthInfo
import waffle.guam.user.service.auth.AuthQueryService

@Component("GuamAnonymous")
class GuamAnonymousGatewayFilterFactory(
    private val authQueryService: AuthQueryService,
) : AbstractGatewayFilterFactory<GuamAnonymousGatewayFilterFactory.Config>(Config::class.java) {
    override fun apply(config: Config?) = GatewayFilter { exchange, chain ->
        mono { chain.filter(addUserHeader(exchange)) }
            .flatMap { it }
    }

    private suspend fun addUserHeader(exchange: ServerWebExchange): ServerWebExchange {
        val requestBuilder = exchange.request.mutate()

        val token = exchange.request.headers.getFirst("Authorization")
            ?.split(" ")
            ?.getOrNull(1)

        val user = if (token == null) {
            AuthInfo.User(userId = 0L, deviceId = null)
        } else {
            authQueryService.getAuthInfo(token).user ?: throw UserUnAuthorized("유저 정보를 찾을 수 없습니다.")
        }

        requestBuilder.header("X-GATEWAY-USER-ID", "${user.userId}")
        requestBuilder.header("X-GATEWAY-DEVICE-ID", user.deviceId)

        return exchange.mutate().request(requestBuilder.build()).build()
    }

    class Config
}
