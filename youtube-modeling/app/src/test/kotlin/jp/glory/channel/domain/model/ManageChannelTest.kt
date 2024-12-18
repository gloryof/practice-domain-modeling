package jp.glory.channel.domain.model

import io.mockk.every
import io.mockk.mockk
import jp.glory.base.domain.DomainErrorCode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.time.OffsetDateTime

class ManageChannelTest {
    @Nested
    inner class TestUpload {
        @Test
        fun success() {
            val ownerId = ChanelOwnerId("owner-id-1")
            val sut = ManageChannel(
                id = ChanelId("test-channel-id"),
                owners = listOf(
                    ownerId,
                    ChanelOwnerId("owner-id-2"),
                    ChanelOwnerId("owner-id-3"),
                )
            )

            val movieTitle = MovieTitle("test-title")
            val releaseAt = ReleaseAt(OffsetDateTime.now().plusDays(1))
            val binary = ByteArrayInputStream(byteArrayOf(1, 1, 1))
            val movieId = MovieId("test-movie-id")

            val idGenerator: MovieIdGenerator = mockk()
            every {
                idGenerator.generate()
            } returns movieId

            val actual = sut.upload(
                uploadUserId = ownerId,
                movieTitle = movieTitle,
                releaseAt = releaseAt,
                binary = binary,
                idGenerator = idGenerator
            ).value

            Assertions.assertTrue(actual.movieId.value.isNotEmpty())
            Assertions.assertEquals(sut.id, actual.chanelId,)
            Assertions.assertEquals(movieTitle, actual.title)
            Assertions.assertEquals(releaseAt, actual.releaseAt)
            Assertions.assertEquals(movieId, actual.movieId)
        }

        @Test
        fun fail() {
            val ownerId = ChanelOwnerId("owner-id-1")
            val sut = ManageChannel(
                id = ChanelId("test-channel-id"),
                owners = emptyList()
            )

            val movieTitle = MovieTitle("test-title")
            val releaseAt = ReleaseAt(OffsetDateTime.now().plusDays(1))
            val binary = ByteArrayInputStream(byteArrayOf(1, 1, 1))

            val actual = sut.upload(
                uploadUserId = ownerId,
                movieTitle = movieTitle,
                releaseAt = releaseAt,
                binary = binary,
                idGenerator = mockk()
            ).error

            Assertions.assertEquals(DomainErrorCode.NotHaveUploadMovieAuthority, actual )
        }
    }
}