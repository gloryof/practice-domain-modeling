package jp.glory.channel.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import jp.glory.base.domain.DomainErrorCode
import jp.glory.base.usecase.AuthorizedUserId
import jp.glory.base.usecase.UsecaseErrorCode
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.event.InvitedChannel
import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.ChannelOwnerId
import jp.glory.channel.domain.model.InvitedUserId
import jp.glory.channel.domain.model.ManageChannel
import jp.glory.channel.domain.repository.ManageChannelRepository
import java.time.Clock
import java.time.OffsetDateTime

class InviteOwnerToChannel(
    private val repository: ManageChannelRepository,
    private val eventListener: ChannelEventListener,
    private val clock: Clock
) {
    fun invite(input: Input): Result<Output, UsecaseErrorCode> =
        repository.findById(ChannelId(input.channelId))
            .flatMap { inviteUser(it, input) }
            .flatMap  { handleEvent(it) }
            .map {
                Output(
                    channelId = it.channelId.value,
                    invitedUserId = it.invitedUserId.value,
                    limitAt = it.limitAt
                )
            }
            .mapError { UsecaseErrorCode.fromDomain(it) }

    private fun inviteUser(
       channel: ManageChannel,
       input: Input
    ): Result<InvitedChannel, DomainErrorCode> =
        channel.invite(
            inviterOwnerId = ChannelOwnerId(input.ownerId.value),
            invitedUserId = InvitedUserId(input.invitedUserId),
            limitAt = OffsetDateTime.now(clock).plusHours(24)
        )

    private fun handleEvent(event: InvitedChannel): Result<InvitedChannel, DomainErrorCode> =
        eventListener.handleInvitedChannel(event)
            .map { event }


    class Input(
        val ownerId: AuthorizedUserId,
        val channelId: String,
        val invitedUserId: String
    )

    class Output(
        val channelId: String,
        val invitedUserId: String,
        val limitAt: OffsetDateTime
    )
}