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

        GET("/api/v1/auth", auth::getAuth)
        GET("/api/v1/user/token", auth::initFirebaseToken)

        // block
        GET("/api/v1/blocks", block::gets)
        POST("/api/v1/blocks", block::create)
        DELETE("/api/v1/blocks", block::delete)

        // push
        GET("/api/v1/push", notification::gets)
        POST("/api/v1/push/read", notification::read)

        // user
        GET("/api/v1/users/me", user::getMe)
        GET("/api/v1/users", user::gets)
        GET("/api/v1/users/{userId}", user::get)
        PATCH("/api/v1/users/{targetUserId}", user::update)
        POST("/api/v1/users/{targetUserId}/interest", user::addInterest)
        DELETE("/api/v1/users/{targetUserId}/interest", user::deleteInterest)
    }
}

fun main() {
    runApplication<GuamUserApplication>()
}
