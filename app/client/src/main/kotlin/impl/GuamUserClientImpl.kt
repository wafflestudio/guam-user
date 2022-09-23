package waffle.guam.user.client.impl

import org.slf4j.LoggerFactory
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import waffle.guam.user.client.GuamUserClient
import waffle.guam.user.domain.AuthInfo
import waffle.guam.user.domain.UserInfo

class GuamUserClientImpl(
    url: String,
    builder: WebClient.Builder,
    private val fallback: Boolean,
) : GuamUserClient {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val client = builder.baseUrl(url).build()

    override suspend fun getUser(userId: Long): UserInfo = runCatching {
        client.get()
            .uri("community/api/v1/users?userIds={userId}", userId)
            .retrieve()
            .awaitBody<List<UserInfo>>()
            .first()
    }.getOrElse {
        logger.error("[GuamUserClient][getUser] failed", it)

        if (fallback) {
            unknownUser
        } else {
            throw it
        }
    }

    override suspend fun getUsers(userIds: List<Long>): Map<Long, UserInfo> = runCatching {
        client.get()
            .uri("community/api/v1/users?userIds={userIds}", userIds.joinToString(","))
            .retrieve()
            .awaitBody<List<UserInfo>>()
            .let { users ->
                val resMap = users.associateBy { it.id }

                userIds.associateWith {
                    resMap[it] ?: run {
                        if (fallback) {
                            unknownUser
                        } else {
                            throw IllegalArgumentException("UserInfo of $it is missing.")
                        }
                    }
                }
            }
    }.getOrElse {
        logger.error("[GuamUserClient][getUser] failed", it)

        if (fallback) {
            userIds.associateWith { unknownUser }
        } else {
            throw it
        }
    }

    private val unknownUser = UserInfo(
        id = -1,
        email = null,
        nickname = "알 수 없는 유저",
        introduction = null,
        githubId = null,
        blogUrl = null,
        profileImage = null,
        interests = listOf()
    )
}
