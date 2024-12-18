package jp.glory.channel.domain.model

class Subscriber(
    val id: SubscriberId,
    private val subscribedChannelIds: List<ChanelId>
) {
    fun isSubscribed(chanelId: ChanelId): Boolean =
        subscribedChannelIds.contains(chanelId)
}

@JvmInline
value class SubscriberId(val value: String)