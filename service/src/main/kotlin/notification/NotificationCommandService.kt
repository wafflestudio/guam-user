package waffle.guam.user.service.notification

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.user.domain.UnAuthorized
import waffle.guam.user.domain.UserNotFound
import waffle.guam.user.infra.db.Notification
import waffle.guam.user.infra.db.Notification.NotificationKind
import waffle.guam.user.infra.db.NotificationRepository
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.service.notification.NotificationCommandService.CreateNotification
import waffle.guam.user.service.notification.NotificationCommandService.ReadNotification

interface NotificationCommandService {
    suspend fun create(command: CreateNotification)
    suspend fun read(command: ReadNotification)

    data class CreateNotification(
        val producerId: Long,
        val infos: List<Info>,
    ) {
        data class Info(
            val consumerId: Long,
            val kind: String,
            val body: String,
            val linkUrl: String,
            val isAnonymousEvent: Boolean,
        )
    }

    data class ReadNotification(
        val userId: Long,
        val notificationIds: List<Long>,
    )
}

@Transactional
@Service
class NotificationCommandServiceImpl(
    private val repository: NotificationRepository,
    private val userRepository: UserRepository,
) : NotificationCommandService {

    override suspend fun create(command: CreateNotification) {
        val producer = userRepository.findById(command.producerId) ?: throw UserNotFound()

        val toCreate = command.infos
            .filterNot { command.producerId == it.consumerId } // 자기 자신에게는 보내지 않는다.
            .map {
                Notification(
                    userId = it.consumerId,
                    writerId = producer.id,
                    kind = NotificationKind.valueOf(it.kind),
                    body = it.body.take(50), // 50자까지만 저장
                    linkUrl = it.linkUrl,
                    isAnonymousEvent = it.isAnonymousEvent,
                    isRead = false
                )
            }

        repository.saveAll(toCreate)
    }

    override suspend fun read(command: ReadNotification) {
        val (mine, others) = repository.findAllByIds(command.notificationIds)
            .partition { it.userId == command.userId }

        if (others.isNotEmpty()) {
            throw UnAuthorized()
        }

        repository.readAll(userId = command.userId, ids = mine.map { it.id })
    }
}
