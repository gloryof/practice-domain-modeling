package jp.glory.channel.usecase

import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.base.usecase.AuthorizedUserId
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.event.UploadedMovie
import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.ChannelOwnerId
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
            channelId = "test-channel-id",
            title = "test-title",
            releaseAt = OffsetDateTime.now(),
            binary = ByteArrayInputStream(byteArrayOf(1, 1, 1))
        )
        val expected = MovieId("test-movie-id")

        val channel = ManageChannel(
            id = ChannelId(input.channelId),
            owners = mutableListOf(ChannelOwnerId("test-user-id")),
            inviting = mutableMapOf()
        )

        val event = UploadedMovie(
            movieId = expected,
            channelId = ChannelId(input.channelId),
            releaseAt = ReleaseAt(input.releaseAt),
            title = MovieTitle(input.title),
            binary = input.binary
        )

        val repository: ManageChannelRepository = mockk()
        every {
            repository.findById(ChannelId(input.channelId))
        } returns Ok(channel)

        val listener: ChannelEventListener = mockk()
        every {
            listener.handleUploadedMovie(event)
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
            listener.handleUploadedMovie(event)
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