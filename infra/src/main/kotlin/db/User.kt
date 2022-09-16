package waffle.guam.user.infra.db

data class User(
    val id: Long = 0L,
    val email: String? = null,
    val nickname: String = "",
    val introduction: String? = null,
    val githubId: String? = null,
    val blogUrl: String? = null,
    val profileImage: String? = null,
    val interests: List<String> = emptyList(),
    val firebaseId: String
)
