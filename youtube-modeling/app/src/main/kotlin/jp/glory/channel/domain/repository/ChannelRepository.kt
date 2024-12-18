package jp.glory.channel.domain.repository

import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.model.ChanelId
import jp.glory.channel.domain.model.Channel

interface ChannelRepository {
    fun findById(channelId: ChanelId): Result<Channel, DomainErrorCode>
}