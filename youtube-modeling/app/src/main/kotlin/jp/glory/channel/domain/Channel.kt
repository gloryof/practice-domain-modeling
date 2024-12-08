package jp.glory.channel.domain

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode

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

data class SubscribedChannel(
    val chanelId: ChanelId,
    val subscriberId: SubscriberId
)

@JvmInline
value class ChanelId(val value:String)

@JvmInline
value class ChanelTitle(val value:String)

@JvmInline
value class SubscriberCount(val value: UInt)

@JvmInline
value class MovieCount(val value: UInt)