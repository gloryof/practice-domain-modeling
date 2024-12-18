package jp.glory.channel.domain.repository

import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.model.Subscriber
import jp.glory.channel.domain.model.SubscriberId

interface SubscriberRepository {
    fun findById(subscriberId: SubscriberId): Result<Subscriber, DomainErrorCode>
}