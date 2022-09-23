package waffle.guam.user.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude

data class UserInfo(
    val id: Long,
    val email: String?,
    val nickname: String,
    val introduction: String?,
    val githubId: String?,
    val blogUrl: String?,
    val profileImage: String?,
    val interests: List<InterestInfo>,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val presignedUrl: String? = null, // for update
) {
    val isProfileSet: Boolean get() = nickname.isNotBlank()

    @get:JsonIgnore
    val isAnonymous: Boolean get() = id == 0L

    data class InterestInfo(
        val name: String,
    )
}
