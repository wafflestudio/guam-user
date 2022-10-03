package waffle.guam.user.infra.db

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Service

interface UserRepository {

    suspend fun findById(userId: Long): User?
    suspend fun findAllByIds(ids: List<Long>): List<User>
    suspend fun findByFirebaseId(firebaseId: String): User?
    suspend fun save(e: User): User
}

@Service
internal class UserRepositoryImpl(
    private val userDao: UserDao,
) : UserRepository {

    override suspend fun findById(userId: Long): User? =
        userDao.findById(userId)?.let(::UserEntity)

    override suspend fun findAllByIds(ids: List<Long>): List<User> =
        userDao.findAllById(ids).map(::UserEntity).toList()

    override suspend fun findByFirebaseId(firebaseId: String): User? =
        userDao.findByFirebaseId(firebaseId)?.let(::UserEntity)

    override suspend fun save(e: User): User =
        userDao.save(UserTable(e)).let(::UserEntity)
}

@Table("users")
internal data class UserTable(
    @Id
    val id: Long = 0L,
    val email: String?,
    var nickname: String,
    var introduction: String?,
    var githubId: String?,
    var blogUrl: String?,
    var profileImage: String?,
    val interests: String?,
    val firebaseId: String,
)

internal interface UserDao : CoroutineCrudRepository<UserTable, Long> {

    suspend fun findByFirebaseId(firebaseId: String): UserTable?
}

internal fun UserEntity(e: UserTable) = User(
    id = e.id,
    email = e.email,
    nickname = e.nickname,
    introduction = e.introduction,
    githubId = e.githubId,
    blogUrl = e.blogUrl,
    profileImage = e.profileImage,
    interests = e.interests?.takeIf { it.isNotBlank() }?.split(",") ?: emptyList(),
    firebaseId = e.firebaseId
)

internal fun UserTable(d: User) = UserTable(
    id = d.id,
    email = d.email,
    nickname = d.nickname,
    introduction = d.introduction,
    githubId = d.githubId,
    blogUrl = d.blogUrl,
    profileImage = d.profileImage,
    interests = d.interests.joinToString(","),
    firebaseId = d.firebaseId,
)
