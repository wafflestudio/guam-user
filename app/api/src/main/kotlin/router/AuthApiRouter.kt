package waffle.guam.user.api.router

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import waffle.guam.user.domain.FirebaseCustomTokenInfo
import waffle.guam.user.service.auth.AuthCommandService
import waffle.guam.user.service.auth.AuthCommandService.CreateUser
import waffle.guam.user.service.auth.AuthQueryService

@Service
class AuthApiRouter(
    private val authCommandService: AuthCommandService,
    private val authQueryService: AuthQueryService,
) {

    suspend fun getAuth(request: ServerRequest): ServerResponse {
        val token = request.getParam("token")

        val response = authQueryService.getAuthInfo(token).takeIf { it.user != null }
            ?: authCommandService.creteAuth(
                CreateUser(token)
            )

        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun initFirebaseToken(request: ServerRequest): ServerResponse {
        val kakaoToken = request.getParam("kakaoToken")

        val response = FirebaseCustomTokenInfo(
            customToken = authCommandService.initFirebaseCustomToken(kakaoToken)
        )

        return ServerResponse.ok().bodyValueAndAwait(response)
    }
}
