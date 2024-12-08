package jp.glory.channel.domain

import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode

interface ChannelEventListener {
    fun handleSubscribed(event: SubscribedChannel): Result<Unit, DomainErrorCode>
}