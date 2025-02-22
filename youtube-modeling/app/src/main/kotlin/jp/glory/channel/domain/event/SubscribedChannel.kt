package jp.glory.channel.domain.event

import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.SubscriberId

data class SubscribedChannel(
    val channelId: ChannelId,
    val subscriberId: SubscriberId
)