package waffle.guam.user.api

import io.wafflestudio.spring.corouter.simpleCoRouter
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import waffle.guam.user.api.router.AuthApiRouter
import waffle.guam.user.api.router.BlockApiRouter
import waffle.guam.user.api.router.NotificationApiRouter
import waffle.guam.user.api.router.UserApiRouter
import java.net.URI

@SpringBootApplication
class GuamUserApplication(
    private val auth: AuthApiRouter,
    private val user: UserApiRouter,
    private val block: BlockApiRouter,
    private val notification: NotificationApiRouter,
) {

    @Bean
    fun indexRouter() = coRouter {
        GET("/") { temporaryRedirect(URI("/swagger-ui.html")).buildAndAwait() }
    }

    @Bean
    fun router() = simpleCoRouter {
        GET("/api/v1/user/token", auth::initFirebaseToken)

        // block
        GET("/community/api/v1/blocks", block::gets)
        POST("/community/api/v1/blocks", block::create)
        DELETE("/community/api/v1/blocks", block::delete)

        // push
        GET("/community/api/v1/push", notification::gets)
        POST("/community/api/v1/push/read", notification::read)

        // user
        GET("/community/api/v1/users/me", user::getMe)
        GET("/community/api/v1/users", user::gets)
        GET("/community/api/v1/users/{userId}", user::get)
        DELETE("/community/api/v1/users/{targetUserId}", user::delete)
        PATCH("/community/api/v1/users/{targetUserId}", user::update)
        POST("/community/api/v1/users/{targetUserId}/interest", user::addInterest)
        DELETE("/community/api/v1/users/{targetUserId}/interest", user::deleteInterest)

        // fixme: /community를 prefix로 안찌르는 서버가 있다. (letter, community)
        GET("/api/v1/users", user::gets)
        GET("/api/v1/users/{userId}", user::get)
        GET("/api/v1/blocks", block::gets)
    }
}

fun main() {
    runApplication<GuamUserApplication>()
}
