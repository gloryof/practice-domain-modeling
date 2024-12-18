package jp.glory.channel.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.zip
import jp.glory.base.domain.DomainErrorCode
import jp.glory.base.usecase.AuthorizedUserId
import jp.glory.base.usecase.UsecaseErrorCode
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.model.ChanelId
import jp.glory.channel.domain.model.Channel
import jp.glory.channel.domain.model.Subscriber
import jp.glory.channel.domain.model.SubscriberId
import jp.glory.channel.domain.repository.ChannelRepository
import jp.glory.channel.domain.repository.SubscriberRepository

class SubscribeChannel(
    private val channelRepository: ChannelRepository,
    private val subscriberRepository: SubscriberRepository,
    private val channelEventListener: ChannelEventListener
) {
    fun subscribe(
        input: Input
    ): Result<Unit, UsecaseErrorCode> =
        zip (
            { channelRepository.findById(ChanelId(input.channelId)) },
            { subscriberRepository.findById(SubscriberId(input.subscriberId.value)) },
            { channel, subscriber -> Target(channel, subscriber) }
        )
            .flatMap { subscribeChannel(it.channel, it.subscriber) }
            .mapError { UsecaseErrorCode.fromDomain(it) }

    private fun subscribeChannel(
        channel: Channel,
        subscriber: Subscriber
    ): Result<Unit, DomainErrorCode> =
        channel.subscribe(subscriber)
            .map { channelEventListener.handleSubscribed(it) }

    class Input(
        val channelId: String,
        val subscriberId: AuthorizedUserId
    )

    private class Target(
        val channel: Channel,
        val subscriber: Subscriber
    )
}