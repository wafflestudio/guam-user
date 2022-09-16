package waffle.guam.user.service.notification

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import waffle.guam.user.domain.NotificationListInfo
import waffle.guam.user.domain.NotificationListInfo.NotificationInfo
import waffle.guam.user.infra.db.NotificationRepository
import waffle.guam.user.service.user.UserQueryService

interface NotificationQueryService {
    suspend fun getNotificationList(userId: Long, page: Int, size: Int): NotificationListInfo
}

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val userQueryService: UserQueryService,
) : NotificationQueryService {

    override suspend fun getNotificationList(userId: Long, page: Int, size: Int): NotificationListInfo {
        val pagedContents = notificationRepository.findAllByUserIdOrderByIdDesc(
            userId = userId,
            pageable = PageRequest.of(page, size)
        )
        val users = userQueryService.getUsers(pagedContents.content.map { it.writerId })
            .associateBy { it.id }

        return NotificationListInfo(
            userId = userId,
            content = pagedContents.content.mapNotNull {
                val writer = users[it.writerId] ?: return@mapNotNull null
                NotificationInfo(
                    id = it.id,
                    userId = it.userId,
                    writer = writer,
                    kind = it.kind.name,
                    body = it.body,
                    linkUrl = it.linkUrl,
                    isRead = it.isRead,
                    createdAt = it.createdAt
                )
            },
            hasNext = pagedContents.hasNext()
        )
    }
}
