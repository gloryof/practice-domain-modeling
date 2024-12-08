package jp.glory.base.usecase

import jp.glory.base.domain.DomainErrorCode

enum class UsecaseErrorCode {
    AlreadyChanelSubscribed,
    ChannelNotFound,
    SubscriberNotFound;

    companion object {
        fun fromDomain(error: DomainErrorCode): UsecaseErrorCode =
            when (error) {
                DomainErrorCode.AlreadyChanelSubscribed -> AlreadyChanelSubscribed
                DomainErrorCode.ChannelNotFound -> ChannelNotFound
                DomainErrorCode.SubscriberNotFound -> SubscriberNotFound
            }
    }
}