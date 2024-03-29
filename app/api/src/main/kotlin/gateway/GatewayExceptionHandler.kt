package waffle.guam.user.api.gateway

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class ExceptionHandler : ErrorWebExceptionHandler {
    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        exchange.response.statusCode = when (ex.also { it.printStackTrace() }) {
            is MissingHeaderException -> HttpStatus.BAD_REQUEST
            is UserNotFoundException, is UserUnAuthorized -> HttpStatus.UNAUTHORIZED
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }

        return exchange.response.setComplete()
    }
}

class MissingHeaderException(msg: String) : RuntimeException(msg)
class UserNotFoundException(msg: String) : RuntimeException(msg)
class UserUnAuthorized(msg: String) : RuntimeException(msg)
