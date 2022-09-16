package waffle.guam.user.domain

data class AuthInfo(
    val user: User?,
) {
    data class User(
        val userId: Long,
        val deviceId: String?,
    )
}
