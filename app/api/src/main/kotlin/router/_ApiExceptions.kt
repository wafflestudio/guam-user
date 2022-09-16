package waffle.guam.user.api.router

abstract class ApiException : RuntimeException() {
    abstract val msg: String
    abstract val status: Int
}

class UnAuthorized(
    override val msg: String = "권한이 없습니다."
) : ApiException() {
    override val status = 403
}

class BadRequest : ApiException() {
    override val msg: String = "잘못된 요청입니다."
    override val status = 400
}
