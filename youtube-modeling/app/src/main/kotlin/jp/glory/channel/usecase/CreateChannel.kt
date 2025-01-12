package jp.glory.channel.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import jp.glory.base.usecase.AuthorizedUserId
import jp.glory.base.usecase.UsecaseErrorCode
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.event.CreatedChannel
import jp.glory.channel.domain.model.ChannelIdIdGenerator
import jp.glory.channel.domain.model.ChannelOwnerId

class CreateChannel(
    private val eventListener: ChannelEventListener,
    private val channelIdIdGenerator: ChannelIdIdGenerator
) {
    fun create(input: Input): Result<Output, UsecaseErrorCode> {
        val event = CreatedChannel(
            channelId = channelIdIdGenerator.generate(),
            ownerId = ChannelOwnerId(input.ownerId.value)
        )

        return eventListener.handleCreatedChannel(event)
            .map { Output(event.channelId.value) }
            .mapError { UsecaseErrorCode.fromDomain(it) }
    }

    class Input(
        val ownerId: AuthorizedUserId
    )

    class Output(
        val channelId: String
    )
}