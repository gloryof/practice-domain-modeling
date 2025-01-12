package jp.glory.channel.domain.event

import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.InvitedUserId
import java.time.OffsetDateTime

data class InvitedChannel(
    val channelId: ChannelId,
    val invitedUserId: InvitedUserId,
    val limitAt: OffsetDateTime
)