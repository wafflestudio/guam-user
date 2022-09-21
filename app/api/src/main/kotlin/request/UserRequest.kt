package waffle.guam.user.api.request

import javax.validation.constraints.Min

data class UpdateUserRequest(
    @Min(value = 2, message = "Nickname should be greater than 1.")
    val nickname: String? = null,
    val introduction: String? = null,
    val githubId: String? = null,
    val blogUrl: String? = null,
    val updateImage: Boolean,
    val imagePath: String? = null,
)

data class CreateInterestRequest(
    val name: String,
)
