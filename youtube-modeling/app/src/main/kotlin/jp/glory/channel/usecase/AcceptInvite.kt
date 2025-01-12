package jp.glory.channel.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.mapError
import jp.glory.base.usecase.UsecaseErrorCode
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.InvitedUserId
import jp.glory.channel.domain.repository.ManageChannelRepository
import java.time.Clock

class AcceptInvite(
    private val repository: ManageChannelRepository,
    private val eventListener: ChannelEventListener,
    private val clock: Clock
) {
    fun accept(input: Input): Result<Unit, UsecaseErrorCode> =
        repository.findById(ChannelId(input.channelId))
            .flatMap {
                it.acceptInvite(
                    invitedUserId = InvitedUserId(input.invitedUserId),
                    clock = clock
                )
            }
            .flatMap { eventListener.handleAcceptedInvite(it) }
            .mapError { UsecaseErrorCode.fromDomain(it) }


    class Input(
        val channelId: String,
        val invitedUserId: String
    )
}