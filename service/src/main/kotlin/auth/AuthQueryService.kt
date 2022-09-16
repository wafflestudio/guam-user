package waffle.guam.user.service.auth

import org.springframework.stereotype.Service
import waffle.guam.user.domain.AuthInfo
import waffle.guam.user.infra.db.UserRepository
import waffle.guam.user.infra.external.FirebaseClient

interface AuthQueryService {
    suspend fun getAuthInfo(firebaseToken: String): AuthInfo
}

@Service
class AuthQueryServiceImpl(
    private val userRepository: UserRepository,
    private val firebase: FirebaseClient,
) : AuthQueryService {

    override suspend fun getAuthInfo(firebaseToken: String): AuthInfo {
        val firebaseUserId = firebase.getUserInfoByToken(firebaseToken)?.userId.let(::requireNotNull)

        return userRepository.findByFirebaseId(firebaseUserId)?.let { user ->
            AuthInfo(AuthInfo.User(userId = user.id, deviceId = null))
        } ?: AuthInfo(null)
    }
}
