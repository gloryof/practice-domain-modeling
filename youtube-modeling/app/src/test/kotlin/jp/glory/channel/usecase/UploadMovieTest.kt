package jp.glory.channel.usecase

import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.base.usecase.AuthorizedUserId
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.event.UploadedMovie
import jp.glory.channel.domain.model.ChanelId
import jp.glory.channel.domain.model.ChanelOwnerId
import jp.glory.channel.domain.model.ManageChannel
import jp.glory.channel.domain.model.MovieId
import jp.glory.channel.domain.model.MovieIdGenerator
import jp.glory.channel.domain.model.MovieTitle
import jp.glory.channel.domain.model.ReleaseAt
import jp.glory.channel.domain.repository.ManageChannelRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.time.OffsetDateTime

class UploadMovieTest {
    @Test
    fun success() {
        val input = UploadMovie.Input(
            authorizedUserId = AuthorizedUserId("test-user-id"),
            chanelId = "test-channel-id",
            title = "test-title",
            releaseAt = OffsetDateTime.now(),
            binary = ByteArrayInputStream(byteArrayOf(1, 1, 1))
        )
        val expected = MovieId("test-movie-id")

        val channel = ManageChannel(
            id = ChanelId(input.chanelId),
            owners = listOf(ChanelOwnerId("test-user-id"))
        )

        val event = UploadedMovie(
            movieId = expected,
            chanelId = ChanelId(input.chanelId),
            releaseAt = ReleaseAt(input.releaseAt),
            title = MovieTitle(input.title),
            binary = input.binary
        )

        val repository: ManageChannelRepository = mockk()
        every {
            repository.findById(ChanelId(input.chanelId))
        } returns Ok(channel)

        val listener: ChannelEventListener = mockk()
        every {
            listener.handleUploaded(event)
        } returns Ok(expected)

        val idGenerator: MovieIdGenerator = mockk()
        every {
            idGenerator.generate()
        } returns expected

        val sut = createSut(
            repository = repository,
            listener = listener,
            idGenerator = idGenerator
        )

        val actual = sut.upload(input).value

        Assertions.assertEquals(expected.value, actual.movieId)
        verify {
            listener.handleUploaded(event)
        }
    }

    private fun createSut(
        repository: ManageChannelRepository = mockk(),
        listener: ChannelEventListener = mockk(),
        idGenerator: MovieIdGenerator = mockk()
    ): UploadMovie = UploadMovie(
        repository = repository,
        listener = listener,
        idGenerator = idGenerator
    )

}