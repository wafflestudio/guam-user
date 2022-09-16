package waffle.guam.user.api.router

import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.queryParamOrNull

fun ServerRequest.getParam(param: String): String = queryParamOrNull(param) ?: throw BadRequest()

fun ServerRequest.getParamInt(param: String): Int = getParam(param).toIntOrNull() ?: throw BadRequest()

fun ServerRequest.getPathLong(pathVar: String) = pathVariable(pathVar).toLongOrNull() ?: throw BadRequest()

fun ServerRequest.getBearerToken(): String = runCatching {
    headers().firstHeader("Authorization")!!.split(" ")[1]
}.getOrElse { throw UnAuthorized() }
