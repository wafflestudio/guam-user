package waffle.guam.user.api

import io.wafflestudio.spring.corouter.RequestContext
import io.wafflestudio.spring.corouter.RequestContextResolver
import kotlinx.coroutines.currentCoroutineContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import waffle.guam.user.service.auth.AuthQueryService
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

data class UserContext(
    val id: Long,
) : RequestContext, CoroutineContext.Element {
    override val key: CoroutineContext.Key<*> get() = UserContext

    companion object : CoroutineContext.Key<UserContext> {
        suspend fun getOrNull(): UserContext? = currentCoroutineContext()[this]
    }
}

@Component
class UserContextResolver(
    private val authQueryService: AuthQueryService,
) : RequestContextResolver {

    override suspend fun resolveContext(serverRequest: ServerRequest): RequestContext {
        val authToken = kotlin.runCatching {
            serverRequest.headers().firstHeader("Authorization")!!
                .split(" ")[1]
        }.getOrElse {
            return EmptyCoroutineContext
        }

        val authInfo = authQueryService.getAuthInfo(authToken)

        return if (authInfo.user != null) {
            UserContext(id = authInfo.user!!.userId)
        } else {
            EmptyCoroutineContext
        }
    }
}
