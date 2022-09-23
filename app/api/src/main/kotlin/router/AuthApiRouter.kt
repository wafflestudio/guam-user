package waffle.guam.user.api.router

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import waffle.guam.user.domain.FirebaseCustomTokenInfo
import waffle.guam.user.service.auth.AuthCommandService

@Service
class AuthApiRouter(
    private val authCommandService: AuthCommandService,
) {

    suspend fun initFirebaseToken(request: ServerRequest): ServerResponse {
        val kakaoToken = request.getParam("kakaoToken")

        val response = FirebaseCustomTokenInfo(
            customToken = authCommandService.initFirebaseCustomToken(kakaoToken)
        )

        return ServerResponse.ok().bodyValueAndAwait(response)
    }
}
