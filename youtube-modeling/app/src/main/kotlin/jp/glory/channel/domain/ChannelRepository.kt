package jp.glory.channel.domain

import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode

interface ChannelRepository {
    fun findById(channelId: ChanelId): Result<Channel, DomainErrorCode>
}