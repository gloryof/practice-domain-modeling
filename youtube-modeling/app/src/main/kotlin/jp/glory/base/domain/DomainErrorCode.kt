package jp.glory.base.domain

enum class DomainErrorCode {
    AlreadyChannelSubscribed,
    ChannelNotFound,
    SubscriberNotFound,
    NotHaveUploadMovieAuthority,
    NotHaveInviteAuthority,
    NotInvited,
    Unknown
}