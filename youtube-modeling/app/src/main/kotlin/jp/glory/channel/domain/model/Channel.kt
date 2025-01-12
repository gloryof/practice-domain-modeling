package jp.glory.channel.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.event.SubscribedChannel

class Channel(
    val id: ChannelId,
    private val title: ChannelTitle,
    private val subscriberCount: SubscriberCount,
    private val movieCount: MovieCount
) {
    fun subscribe(subscriber: Subscriber): Result<SubscribedChannel, DomainErrorCode> {
        if (subscriber.isSubscribed(id)) {
            return Err(DomainErrorCode.AlreadyChannelSubscribed)
        }

        return Ok(
            SubscribedChannel(
                channelId = id,
                subscriberId = subscriber.id
            )
        )
    }
}

@JvmInline
value class ChannelTitle(val value:String)

@JvmInline
value class SubscriberCount(val value: UInt)

@JvmInline
value class MovieCount(val value: UInt)