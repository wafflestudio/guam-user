package waffle.guam.user.domain

abstract class UserException : RuntimeException() {
    abstract val msg: String
    abstract val status: Int
}

class UnAuthorized : UserException() {
    override val msg: String = "권한이 없습니다."
    override val status = 403
}

class UserNotFound : UserException() {
    override val msg: String = "해당 유저가 존재하지 않습니다."
    override val status = 404
}

class DuplicateUser : UserException() {
    override val msg: String = "이미 존재하는 유저입니다."
    override val status = 409
}

class InterestNotFound : UserException() {
    override val msg: String = "해당 관심사가 존재하지 않습니다."
    override val status = 404
}

class DuplicateInterest : UserException() {
    override val msg: String = "이미 존재하는 관심사입니다."
    override val status = 409
}
