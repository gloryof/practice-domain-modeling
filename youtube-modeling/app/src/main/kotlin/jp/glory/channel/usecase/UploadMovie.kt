package jp.glory.channel.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import jp.glory.base.usecase.AuthorizedUserId
import jp.glory.base.usecase.UsecaseErrorCode
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.ChannelOwnerId
import jp.glory.channel.domain.model.MovieIdGenerator
import jp.glory.channel.domain.model.MovieTitle
import jp.glory.channel.domain.model.ReleaseAt
import jp.glory.channel.domain.repository.ManageChannelRepository
import java.io.InputStream
import java.time.OffsetDateTime

class UploadMovie(
    private val repository: ManageChannelRepository,
    private val listener: ChannelEventListener,
    private val idGenerator: MovieIdGenerator
) {
    fun upload(input: Input): Result<Output, UsecaseErrorCode> =
        repository.findById(ChannelId(input.channelId))
            .flatMap {
                it.upload(
                    uploadUserId = ChannelOwnerId(input.authorizedUserId.value),
                    movieTitle = MovieTitle(input.title),
                    releaseAt = ReleaseAt(input.releaseAt),
                    binary = input.binary,
                    idGenerator = idGenerator
                )
            }
            .flatMap { listener.handleUploadedMovie(it) }
            .map { Output(it.value) }
            .mapError { UsecaseErrorCode.fromDomain(it) }

    class Input(
        val authorizedUserId: AuthorizedUserId,
        val channelId: String,
        val title: String,
        val releaseAt: OffsetDateTime,
        val binary: InputStream,
    )

    class Output(
        val movieId: String
    )
}