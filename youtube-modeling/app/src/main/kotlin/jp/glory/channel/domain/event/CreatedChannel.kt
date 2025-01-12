package jp.glory.channel.domain.event

import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.ChannelOwnerId

data class CreatedChannel(
    val channelId: ChannelId,
    val ownerId: ChannelOwnerId
)