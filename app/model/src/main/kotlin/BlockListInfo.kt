package waffle.guam.user.domain

data class BlockListInfo(
    val userId: Long,
    val blockUsers: List<UserInfo>,
)
