package jp.glory.channel.domain.event

import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.InvitedUserId

data class AcceptedInvite(
    val channelId: ChannelId,
    val invitedUserId: InvitedUserId
)