package waffle.guam.user.domain

data class UserInfo(
    val id: Long,
    val email: String?,
    val nickname: String,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
    val profileImage: String?,
    val interests: List<InterestInfo>,
) {
    val isProfileSet: Boolean get() = nickname.isNotBlank()

    data class InterestInfo(
        val name: String,
    )
}
