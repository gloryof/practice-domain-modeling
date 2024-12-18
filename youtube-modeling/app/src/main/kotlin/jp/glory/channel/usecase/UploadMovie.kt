package jp.glory.channel.usecase

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.michaelbull.result.mapError
import jp.glory.base.usecase.AuthorizedUserId
import jp.glory.base.usecase.UsecaseErrorCode
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.model.ChanelId
import jp.glory.channel.domain.model.ChanelOwnerId
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
        repository.findById(ChanelId(input.chanelId))
            .flatMap {
                it.upload(
                    uploadUserId = ChanelOwnerId(input.authorizedUserId.value),
                    movieTitle = MovieTitle(input.title),
                    releaseAt = ReleaseAt(input.releaseAt),
                    binary = input.binary,
                    idGenerator = idGenerator
                )
            }
            .flatMap { listener.handleUploaded(it) }
            .map { Output(it.value) }
            .mapError { UsecaseErrorCode.fromDomain(it) }

    class Input(
        val authorizedUserId: AuthorizedUserId,
        val chanelId: String,
        val title: String,
        val releaseAt: OffsetDateTime,
        val binary: InputStream,
    )

    class Output(
        val movieId: String
    )
}