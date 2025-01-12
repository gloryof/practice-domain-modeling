package jp.glory.channel.domain.model

class Subscriber(
    val id: SubscriberId,
    private val subscribedChannelIds: List<ChannelId>
) {
    fun isSubscribed(channelId: ChannelId): Boolean =
        subscribedChannelIds.contains(channelId)
}

@JvmInline
value class SubscriberId(val value: String)