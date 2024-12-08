package jp.glory.base.usecase

class AuthorizedUser(
    val id: AuthorizedUserId
)

@JvmInline
value class AuthorizedUserId(val value: String)