package jp.glory.channel.domain.event

import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.model.MovieId

interface ChannelEventListener {
    fun handleSubscribed(event: SubscribedChannel): Result<Unit, DomainErrorCode>
    fun handleUploaded(event: UploadedMovie): Result<MovieId, DomainErrorCode>
}