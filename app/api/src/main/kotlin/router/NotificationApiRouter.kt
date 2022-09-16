package waffle.guam.user.api.router

import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import waffle.guam.user.api.UserContext
import waffle.guam.user.api.request.ReadNotification
import waffle.guam.user.api.request.ReadNotificationRequest
import waffle.guam.user.domain.UnAuthorized
import waffle.guam.user.service.notification.NotificationCommandService
import waffle.guam.user.service.notification.NotificationQueryService

@Service
class NotificationApiRouter(
    private val queryService: NotificationQueryService,
    private val commandService: NotificationCommandService,
) {

    suspend fun gets(request: ServerRequest): ServerResponse {
        val uc = UserContext.getOrNull() ?: throw UnAuthorized()
        val page = request.getParamInt("page")
        val size = request.getParamInt("size")

        val response = queryService.getNotificationList(
            userId = uc.id,
            page = page,
            size = size
        )

        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun read(request: ServerRequest): ServerResponse {
        val uc = UserContext.getOrNull() ?: throw UnAuthorized()
        val req = request.awaitBody<ReadNotificationRequest>()

        if (uc.id != req.userId) {
            throw UnAuthorized()
        }

        commandService.read(command = ReadNotification(req))

        return ServerResponse.ok().bodyValueAndAwait(Unit)
    }
}
