package jp.glory.channel.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.zip
import jp.glory.base.domain.DomainErrorCode
import jp.glory.base.usecase.UsecaseErrorCode
import jp.glory.channel.domain.ChanelId
import jp.glory.channel.domain.Channel
import jp.glory.channel.domain.ChannelEventListener
import jp.glory.channel.domain.ChannelRepository
import jp.glory.channel.domain.Subscriber
import jp.glory.channel.domain.SubscriberId
import jp.glory.channel.domain.SubscriberRepository

class SubscribeChannel(
    private val channelRepository: ChannelRepository,
    private val subscriberRepository: SubscriberRepository,
    private val channelEventListener: ChannelEventListener
) {
    fun subscribe(
        channelId: String,
        subscriberId: String
    ): Result<Unit, UsecaseErrorCode> =
        zip (
            { channelRepository.findById(ChanelId(channelId)) },
            { subscriberRepository.findById(SubscriberId(subscriberId)) },
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

    private class Target(
        val channel: Channel,
        val subscriber: Subscriber
    )
}