package waffle.guam.user.infra.db

import java.time.Instant

class Notification(
    val id: Long = 0L,
    val userId: Long,
    val writerId: Long,
    val kind: NotificationKind,
    val body: String,
    val linkUrl: String,
    val isAnonymousEvent: Boolean,
    val isRead: Boolean = false,
    val createdAt: Instant = Instant.now(),
) {
    enum class NotificationKind {
        POST_LIKE, POST_COMMENT, POST_COMMENT_MENTION, POST_COMMENT_LIKE, POST_SCRAP
    }
}
