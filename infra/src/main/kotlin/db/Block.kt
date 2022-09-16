package waffle.guam.user.infra.db

data class Block(
    val id: Long = 0L,
    val userId: Long,
    val blockUserId: Long,
)
