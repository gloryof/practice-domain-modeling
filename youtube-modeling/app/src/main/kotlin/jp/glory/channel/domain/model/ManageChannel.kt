package jp.glory.channel.domain.model

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.event.UploadedMovie
import java.io.InputStream
import java.time.OffsetDateTime
import java.util.UUID

class ManageChannel(
    val id: ChanelId,
    val owners: List<ChanelOwnerId>
) {
    fun upload(
        uploadUserId: ChanelOwnerId,
        movieTitle: MovieTitle,
        releaseAt: ReleaseAt,
        binary: InputStream,
        idGenerator: MovieIdGenerator
    ): Result<UploadedMovie, DomainErrorCode> =
        if (!owners.contains(uploadUserId)) {
            Err(DomainErrorCode.NotHaveUploadMovieAuthority)
        } else {
            Ok(
                UploadedMovie(
                    movieId = idGenerator.generate(),
                    chanelId = id,
                    title = movieTitle,
                    releaseAt = releaseAt,
                    binary = binary
                )
            )
        }
}

class MovieIdGenerator {
    fun generate(): MovieId =
        MovieId(UUID.randomUUID().toString())
}

@JvmInline
value class ChanelOwnerId(val value: String)

class MovieId(val value: String)

@JvmInline
value class MovieTitle(val value: String) {
    init {
        require(value.length < 100)
    }
}

@JvmInline
value class ReleaseAt(val value: OffsetDateTime)