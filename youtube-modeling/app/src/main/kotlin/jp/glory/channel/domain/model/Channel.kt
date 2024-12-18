package jp.glory.channel.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.event.SubscribedChannel

class Channel(
    val id: ChanelId,
    val title: ChanelTitle,
    val subscriberCount: SubscriberCount,
    val movieCount: MovieCount
) {
    fun subscribe(subscriber: Subscriber): Result<SubscribedChannel, DomainErrorCode> {
        if (subscriber.isSubscribed(id)) {
            return Err(DomainErrorCode.AlreadyChanelSubscribed)
        }

        return Ok(
            SubscribedChannel(
                chanelId = id,
                subscriberId = subscriber.id
            )
        )
    }
}

@JvmInline
value class ChanelTitle(val value:String)

@JvmInline
value class SubscriberCount(val value: UInt)

@JvmInline
value class MovieCount(val value: UInt)