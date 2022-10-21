package waffle.guam.user.api.router

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import waffle.guam.user.api.UserContext
import waffle.guam.user.domain.UserNotFound
import waffle.guam.user.service.auth.AuthCommandService
import waffle.guam.user.service.auth.AuthCommandService.CreateUser
import waffle.guam.user.service.auth.AuthQueryService
import waffle.guam.user.service.user.UserCommandService
import waffle.guam.user.service.user.UserCommandService.CreateInterest
import waffle.guam.user.service.user.UserCommandService.DeleteInterest
import waffle.guam.user.service.user.UserCommandService.UpdateUser
import waffle.guam.user.service.user.UserCommandService.DeleteUser
import waffle.guam.user.service.user.UserQueryService
import javax.validation.constraints.Min

@Service
class UserApiRouter(
    private val userQueryService: UserQueryService,
    private val userCommandService: UserCommandService,
    private val authCommandService: AuthCommandService,
    private val authQueryService: AuthQueryService,
) {

    // users/me는 유저 데이터가 생성되기 이전에도 호출될 수 있기 때문에 getOrCreate 적용
    suspend fun getMe(request: ServerRequest): ServerResponse {
        val authToken = runCatching {
            request.headers().firstHeader("Authorization")!!.split(" ")[1]
        }.getOrNull() ?: throw UnAuthorized()

        val authInfo = authQueryService.getAuthInfo(authToken).takeIf { it.user != null }
            ?: authCommandService.creteAuth(CreateUser(authToken))

        val user = userQueryService.getUser(authInfo.user!!.userId) ?: throw UserNotFound()

        return ServerResponse.ok().bodyValueAndAwait(user)
    }

    suspend fun get(request: ServerRequest): ServerResponse {
        val userId = request.getPathLong("userId")

        val user = userQueryService.getUser(userId) ?: throw UserNotFound()

        return ServerResponse.ok().bodyValueAndAwait(user)
    }

    suspend fun gets(request: ServerRequest): ServerResponse {
        val userIdStr = request.getParam("userIds")

        val userIds = userIdStr.takeIf { it.isNotBlank() }
            ?.split(",")
            ?.map { it.toLong() }
            ?: emptyList()

        val users = userQueryService.getUsers(userIds)

        return ServerResponse.ok().bodyValueAndAwait(users)
    }

    suspend fun update(request: ServerRequest): ServerResponse {
        val uc = UserContext.getOrNull() ?: throw UnAuthorized()
        val targetUserId = request.getPathLong("targetUserId")

        if (uc.id != targetUserId) {
            throw UnAuthorized()
        }

        val req = request.awaitBody<UpdateUserRequest>()
        val updatedUser = userCommandService.updateUser(
            UpdateUser(
                userId = targetUserId,
                nickname = req.nickname,
                introduction = req.introduction,
                githubId = req.githubId,
                blogUrl = req.blogUrl,
                updateImage = req.updateImage,
                imagePath = req.imagePath
            )
        )

        return ServerResponse.ok().bodyValueAndAwait(updatedUser)
    }

    suspend fun delete(request: ServerRequest): ServerResponse {
        val uc = UserContext.getOrNull() ?: throw UnAuthorized()
        val targetUserId = request.getPathLong("targetUserId")

        if (uc.id != targetUserId) {
            throw UnAuthorized()
        }

        val deletedUser = userCommandService.deleteUser(
            DeleteUser(
                userId = targetUserId,
            )
        )

        return ServerResponse.ok().bodyValueAndAwait(deletedUser)
    }

    suspend fun addInterest(request: ServerRequest): ServerResponse {
        val uc = UserContext.getOrNull() ?: throw waffle.guam.user.domain.UnAuthorized()
        val targetUserId = request.getPathLong("targetUserId")

        if (uc.id != targetUserId) {
            throw UnAuthorized()
        }

        val req = request.awaitBody<CreateInterestRequest>()

        val updatedUser = userCommandService.createInterest(
            CreateInterest(
                userId = targetUserId,
                interest = req.name
            )
        )

        return ServerResponse.ok().bodyValueAndAwait(updatedUser)
    }

    suspend fun deleteInterest(request: ServerRequest): ServerResponse {
        val uc = UserContext.getOrNull() ?: throw waffle.guam.user.domain.UnAuthorized()
        val targetUserId = request.getPathLong("targetUserId")

        if (uc.id != targetUserId) {
            throw UnAuthorized()
        }

        val updatedUser = userCommandService.deleteInterest(
            DeleteInterest(
                userId = targetUserId,
                interest = request.queryParamOrNull("name") ?: throw BadRequest()
            )
        )

        return ServerResponse.ok().bodyValueAndAwait(updatedUser)
    }
}

data class UpdateUserRequest(
    @Min(value = 2, message = "Nickname should be greater than 1.")
    val nickname: String? = null,
    val introduction: String? = null,
    val githubId: String? = null,
    val blogUrl: String? = null,
    val updateImage: Boolean,
    val imagePath: String? = null,
)

data class CreateInterestRequest(
    val name: String,
)
