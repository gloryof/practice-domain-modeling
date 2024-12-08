package jp.glory.channel.domain

import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode

interface SubscriberRepository {
    fun findById(subscriberId: SubscriberId): Result<Subscriber, DomainErrorCode>
}