package waffle.guam.user.infra.external

import com.google.api.core.ApiFuture
import com.google.api.core.ApiFutureToListenableFuture
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.AuthErrorCode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserRecord.CreateRequest
import kotlinx.coroutines.guava.await
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import waffle.guam.user.infra.external.FirebaseClient.FirebaseUserId

interface FirebaseClient {
    suspend fun getCustomToken(uid: String): String
    suspend fun getUserInfoByUserId(userId: String): FirebaseUserId?
    suspend fun getUserInfoByToken(token: String): FirebaseUserId?
    suspend fun createUserInfo(uid: String): FirebaseUserId

    class FirebaseTokenExpired : RuntimeException()

    data class FirebaseUserId(val userId: String)
}

@Service
class FirebaseClientImpl : FirebaseClient {
    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(ClassPathResource("waffle-guam-firebase-adminsdk-1o1hg-27c33a640a.json").inputStream))
                .build()
        )
        FirebaseAuth.getInstance()
    }

    override suspend fun getCustomToken(uid: String): String =
        firebaseAuth.createCustomTokenAsync(uid).let(::ApiFutureToListenableFuture).await()

    override suspend fun getUserInfoByUserId(userId: String): FirebaseUserId? =
        runCatching {
            firebaseAuth.getUserAsync(userId).await().uid
        }.recover {
            if (it is FirebaseAuthException && it.authErrorCode == AuthErrorCode.USER_NOT_FOUND) {
                null
            } else {
                throw it
            }
        }.getOrThrow()?.let(::FirebaseUserId)

    override suspend fun getUserInfoByToken(token: String): FirebaseUserId? =
        runCatching {
            firebaseAuth.verifyIdTokenAsync(token).await().uid
        }.recover {
            if (it is FirebaseAuthException && it.authErrorCode == AuthErrorCode.USER_NOT_FOUND) {
                null
            } else if (it is FirebaseAuthException && it.authErrorCode == AuthErrorCode.EXPIRED_ID_TOKEN) {
                throw FirebaseClient.FirebaseTokenExpired()
            } else {
                throw it
            }
        }.getOrThrow()?.let(::FirebaseUserId)

    override suspend fun createUserInfo(uid: String): FirebaseUserId =
        firebaseAuth.createUserAsync(CreateRequest().setUid(uid))
            .await()
            .let { FirebaseUserId(it.uid) }

    private suspend fun <T> ApiFuture<T>.await() = ApiFutureToListenableFuture(this).await()
}
