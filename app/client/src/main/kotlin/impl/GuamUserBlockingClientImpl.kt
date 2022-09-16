package waffle.guam.user.client.impl

import kotlinx.coroutines.runBlocking
import org.springframework.web.reactive.function.client.WebClient
import waffle.guam.user.client.GuamUserClient
import waffle.guam.user.domain.AuthInfo
import waffle.guam.user.domain.UserInfo

class GuamUserBlockingClientImpl(
    url: String,
    builder: WebClient.Builder,
    fallback: Boolean,
) : GuamUserClient.Blocking {

    private val client = GuamUserClientImpl(url, builder, fallback)

    override fun getAuth(firebaseToken: String): AuthInfo = runBlocking {
        client.getAuth(firebaseToken)
    }

    override fun getUser(userId: Long): UserInfo = runBlocking {
        client.getUser(userId)
    }

    override fun getUsers(userIds: List<Long>): Map<Long, UserInfo> = runBlocking {
        client.getUsers(userIds)
    }
}
