package jp.glory.channel.domain.event

import jp.glory.channel.domain.model.ChanelId
import jp.glory.channel.domain.model.SubscriberId

data class SubscribedChannel(
    val chanelId: ChanelId,
    val subscriberId: SubscriberId
)