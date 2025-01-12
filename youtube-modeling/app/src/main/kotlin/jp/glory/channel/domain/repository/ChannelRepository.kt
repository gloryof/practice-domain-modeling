package jp.glory.channel.domain.repository

import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.model.Channel
import jp.glory.channel.domain.model.ChannelId

interface ChannelRepository {
    fun findById(channelId: ChannelId): Result<Channel, DomainErrorCode>
}