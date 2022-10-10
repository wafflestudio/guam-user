package waffle.guam.user.service.user

import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.user.domain.DuplicateInterest
import waffle.guam.user.domain.InterestNotFound
import waffle.guam.user.domain.UserInfo
import waffle.guam.user.domain.UserNotFound
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.infra.external.S3PresignClient
import waffle.guam.user.service.user.UserCommandService.CreateInterest
import waffle.guam.user.service.user.UserCommandService.DeleteInterest
import waffle.guam.user.service.user.UserCommandService.UpdateUser
import waffle.guam.user.service.user.UserCommandService.DeleteUser

interface UserCommandService {
    suspend fun updateUser(command: UpdateUser): UserInfo
    suspend fun deleteUser(command: DeleteUser)
    suspend fun createInterest(command: CreateInterest): UserInfo
    suspend fun deleteInterest(command: DeleteInterest): UserInfo

    data class UpdateUser(
        val userId: Long,
        val nickname: String?,
        val introduction: String?,
        val githubId: String?,
        val blogUrl: String?,
        val updateImage: Boolean,
        val imagePath: String?,
    )

    data class DeleteUser(
        val userId: Long,
    )

    data class CreateInterest(
        val userId: Long,
        val interest: String,
    )

    data class DeleteInterest(
        val userId: Long,
        val interest: String,
    )
}

@Transactional
@Service
class UserCommandServiceImpl(
    private val userRepository: UserRepository,
    private val s3PresignClient: S3PresignClient,
    env: Environment,
) : UserCommandService {
    private val imagePrefix = when {
        env.acceptsProfiles(Profiles.of("dev")) -> "DEV"
        env.acceptsProfiles(Profiles.of("prod")) -> "PROD"
        else -> "LOCAL"
    }

    override suspend fun updateUser(command: UpdateUser): UserInfo {
        val user = userRepository.findById(command.userId) ?: throw UserNotFound()
        val (profileImage, presigendUrl) = when (command.updateImage) {
            true -> {
                if (command.imagePath != null) {
                    val path = "$imagePrefix/USER/${command.userId}/${command.imagePath}"
                    path to s3PresignClient.getPresigendUrl(path)
                } else {
                    null to null
                }
            }

            false -> {
                user.profileImage to null
            }
        }

        val updatedUser = user.copy(
            nickname = command.nickname ?: user.nickname,
            introduction = command.introduction ?: user.introduction,
            githubId = command.githubId ?: user.githubId,
            blogUrl = command.blogUrl ?: user.blogUrl,
            profileImage = profileImage
        )

        return userRepository.save(updatedUser).let(::UserInfo).copy(presignedUrl = presigendUrl)
    }

    override suspend fun deleteUser(command: DeleteUser) {
        userRepository.deleteById(command.userId)
    }

    override suspend fun createInterest(command: CreateInterest): UserInfo {
        val user = userRepository.findById(command.userId) ?: throw UserNotFound()

        if (command.interest in user.interests) {
            throw DuplicateInterest()
        }

        val updatedUser = user.copy(
            interests = listOf(command.interest) + user.interests
        )

        return userRepository.save(updatedUser).let(::UserInfo)
    }

    override suspend fun deleteInterest(command: DeleteInterest): UserInfo {
        val user = userRepository.findById(command.userId) ?: throw UserNotFound()

        if (command.interest !in user.interests) {
            throw InterestNotFound()
        }

        val updatedUser = user.copy(
            interests = user.interests.filter { it != command.interest }
        )

        return userRepository.save(updatedUser).let(::UserInfo)
    }
}
