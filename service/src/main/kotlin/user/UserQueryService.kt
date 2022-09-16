package waffle.guam.user.service.user

import org.springframework.stereotype.Service
import waffle.guam.user.domain.UserInfo
import waffle.guam.user.domain.UserInfo.InterestInfo
import waffle.guam.user.infra.db.User
import waffle.guam.user.infra.db.UserRepository

interface UserQueryService {
    suspend fun getUser(userId: Long): UserInfo?
    suspend fun getUser(firebaseId: String): UserInfo?
    suspend fun getUsers(userIds: List<Long>): List<UserInfo>
}

@Service
class UserQueryServiceImpl(
    private val userRepository: UserRepository,
) : UserQueryService {

    override suspend fun getUser(userId: Long): UserInfo? =
        userRepository.findById(userId)?.let(::UserInfo)

    override suspend fun getUser(firebaseId: String): UserInfo? =
        userRepository.findByFirebaseId(firebaseId)?.let(::UserInfo)

    override suspend fun getUsers(userIds: List<Long>): List<UserInfo> =
        userRepository.findAllByIds(userIds).map(::UserInfo)
}

fun UserInfo(user: User): UserInfo = UserInfo(
    id = user.id,
    email = user.email,
    nickname = user.nickname,
    introduction = user.introduction,
    githubId = user.githubId,
    blogUrl = user.blogUrl,
    profileImage = user.profileImage,
    interests = user.interests.map(::InterestInfo)
)
