package waffle.guam.user.infra.db

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Service
import waffle.guam.user.infra.db.Notification.NotificationKind
import java.time.Instant

interface NotificationRepository {

    suspend fun saveAll(notifications: List<Notification>): List<Notification>
    suspend fun readAll(userId: Long, ids: List<Long>)
    suspend fun findAllByIds(ids: List<Long>): List<Notification>

    suspend fun findAllByUserIdOrderByIdDesc(userId: Long, pageable: Pageable): Page<Notification>
}

@Service
internal class NotificationRepositoryImpl(
    private val notificationDao: NotificationDao,
    private val databaseClient: DatabaseClient,
) : NotificationRepository {
    override suspend fun saveAll(notifications: List<Notification>): List<Notification> =
        notificationDao.saveAll(notifications.map { it.toTable() }).toList().map { it.toDomain() }

    override suspend fun readAll(userId: Long, ids: List<Long>) {
        databaseClient.sql(
            "UPDATE push_events SET is_read = true WHERE user_id = :userId AND id in (:ids)"
        )
            .bind("userId", userId)
            .bind("ids", ids)
            .fetch()
            .awaitOneOrNull()
    }

    override suspend fun findAllByIds(ids: List<Long>): List<Notification> =
        notificationDao.findAllById(ids).map { it.toDomain() }.toList()

    override suspend fun findAllByUserIdOrderByIdDesc(userId: Long, pageable: Pageable): Page<Notification> {
        val contents = notificationDao.findAllByUserIdOrderByIdDesc(userId, pageable).toList()
        val total = notificationDao.countAllByUserId(userId)

        return PageImpl(contents.map { it.toDomain() }, pageable, total)
    }
}

internal interface NotificationDao : CoroutineCrudRepository<NotificationTable, Long> {
    fun findAllByUserIdOrderByIdDesc(userId: Long, pageable: Pageable): Flow<NotificationTable>
    suspend fun countAllByUserId(userId: Long): Long
}

@Table("push_events")
internal data class NotificationTable(
    @Id
    val id: Long = 0L,
    val userId: Long,
    val writerId: Long,
    val kind: String,
    val body: String,
    val linkUrl: String,
    val isAnonymousEvent: Boolean,
    var isRead: Boolean = false,
    val createdAt: Instant = Instant.now(),
)

internal fun Notification.toTable() = NotificationTable(
    id = id,
    userId = userId,
    writerId = writerId,
    kind = kind.name,
    body = body,
    linkUrl = linkUrl,
    isAnonymousEvent = isAnonymousEvent,
    isRead = isRead,
    createdAt = createdAt
)

internal fun NotificationTable.toDomain() = Notification(
    id = id,
    userId = userId,
    writerId = writerId,
    kind = NotificationKind.valueOf(kind),
    body = body,
    linkUrl = linkUrl,
    isAnonymousEvent = isAnonymousEvent,
    isRead = isRead,
    createdAt = createdAt
)
