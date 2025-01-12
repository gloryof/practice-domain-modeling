package jp.glory.channel.domain.repository

import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.ManageChannel

interface ManageChannelRepository {
    fun findById(channelId: ChannelId): Result<ManageChannel, DomainErrorCode>
}