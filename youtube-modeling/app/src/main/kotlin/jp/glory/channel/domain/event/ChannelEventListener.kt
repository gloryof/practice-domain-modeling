package jp.glory.channel.domain.event

import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.model.MovieId

interface ChannelEventListener {
    fun handleSubscribedChannel(event: SubscribedChannel): Result<Unit, DomainErrorCode>
    fun handleUploadedMovie(event: UploadedMovie): Result<MovieId, DomainErrorCode>
    fun handleCreatedChannel(event: CreatedChannel): Result<Unit, DomainErrorCode>
    fun handleInvitedChannel(event: InvitedChannel): Result<Unit, DomainErrorCode>
    fun handleAcceptedInvite(event: AcceptedInvite): Result<Unit, DomainErrorCode>
}