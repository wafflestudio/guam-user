package waffle.guam.user.client

import waffle.guam.user.domain.AuthInfo
import waffle.guam.user.domain.UserInfo

interface GuamUserClient {
    suspend fun getAuth(firebaseToken: String): AuthInfo
    suspend fun getUser(userId: Long): UserInfo
    suspend fun getUsers(userIds: List<Long>): Map<Long, UserInfo>

    interface Blocking {
        fun getAuth(firebaseToken: String): AuthInfo
        fun getUser(userId: Long): UserInfo
        fun getUsers(userIds: List<Long>): Map<Long, UserInfo>
    }
}
