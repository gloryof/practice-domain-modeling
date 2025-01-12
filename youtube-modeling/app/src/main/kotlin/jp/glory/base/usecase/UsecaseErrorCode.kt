package jp.glory.base.usecase

import jp.glory.base.domain.DomainErrorCode

enum class UsecaseErrorCode {
    AlreadyChannelSubscribed,
    ChannelNotFound,
    SubscriberNotFound,
    NotHaveUploadMovieAuthority,
    NotHaveInviteAuthority,
    NotInvited,
    Unknown;

    companion object {
        fun fromDomain(error: DomainErrorCode): UsecaseErrorCode =
            when (error) {
                DomainErrorCode.AlreadyChannelSubscribed -> AlreadyChannelSubscribed
                DomainErrorCode.ChannelNotFound -> ChannelNotFound
                DomainErrorCode.SubscriberNotFound -> SubscriberNotFound
                DomainErrorCode.NotHaveUploadMovieAuthority -> NotHaveUploadMovieAuthority
                DomainErrorCode.NotHaveInviteAuthority -> NotHaveInviteAuthority
                DomainErrorCode.NotInvited -> NotInvited
                DomainErrorCode.Unknown -> Unknown
            }
    }
}