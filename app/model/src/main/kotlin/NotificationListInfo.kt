package waffle.guam.user.domain

import java.time.Instant

data class NotificationListInfo(
    val userId: Long,
    val content: List<NotificationInfo>,
    val hasNext: Boolean,
) {

    data class NotificationInfo(
        val id: Long = 0L,
        val userId: Long,
        val writer: UserInfo,
        val kind: String,
        val body: String,
        val linkUrl: String,
        val isRead: Boolean,
        val createdAt: Instant,
    )
}
