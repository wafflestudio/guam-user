package waffle.guam.user.service.auth

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import waffle.guam.user.domain.AuthInfo
import waffle.guam.user.domain.DuplicateUser
import waffle.guam.user.infra.db.User
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.infra.external.FirebaseClient
import waffle.guam.user.infra.external.KakaoClient
import waffle.guam.user.service.auth.AuthCommandService.CreateUser

interface AuthCommandService {
    suspend fun creteAuth(command: CreateUser): AuthInfo

    suspend fun initFirebaseCustomToken(kakaoToken: String): String
    data class CreateUser(val firebaseToken: String)
}

@Transactional
@Service
class AuthCommandServiceImpl(
    private val userRepository: UserRepository,
    private val kakao: KakaoClient,
    private val firebase: FirebaseClient,
) : AuthCommandService {
    override suspend fun creteAuth(command: CreateUser): AuthInfo {
        val firebaseUserId = firebase.getUserInfoByToken(command.firebaseToken)?.userId.let(::requireNotNull)

        userRepository.findByFirebaseId(firebaseUserId)?.let { throw DuplicateUser() }

        return AuthInfo(
            user = AuthInfo.User(
                userId = userRepository.save(User(firebaseId = firebaseUserId)).id,
                deviceId = null
            )
        )
    }

    override suspend fun initFirebaseCustomToken(kakaoToken: String): String {
        val kakaoId = kakao.getUserId(kakaoToken).let(::requireNotNull)
        val firebaseUid = "guam:$kakaoId"

        return firebase.run { getUserInfoByUserId(firebaseUid) ?: createUserInfo(firebaseUid) }
            .let { firebase.getCustomToken(it.userId) }
    }
}
