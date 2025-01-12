package jp.glory.channel.domain.model

import io.mockk.every
import io.mockk.mockk
import jp.glory.base.domain.DomainErrorCode
import jp.glory.channel.domain.event.AcceptedInvite
import jp.glory.channel.domain.event.InvitedChannel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.io.ByteArrayInputStream
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset

class ManageChannelTest {
    @Nested
    inner class TestUpload {
        @Test
        fun success() {
            val ownerId = ChannelOwnerId("owner-id-1")
            val sut = createSut(
                owners = mutableListOf(
                    ownerId,
                    ChannelOwnerId("owner-id-2"),
                    ChannelOwnerId("owner-id-3"),
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
            Assertions.assertEquals(sut.id, actual.channelId,)
            Assertions.assertEquals(movieTitle, actual.title)
            Assertions.assertEquals(releaseAt, actual.releaseAt)
            Assertions.assertEquals(movieId, actual.movieId)
        }

        @Test
        fun fail() {
            val ownerId = ChannelOwnerId("owner-id-1")
            val sut = createSut(
                owners = mutableListOf()
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

    @Nested
    inner class TestInvite {
        @Nested
        inner class Success {
            @Test
            fun `If new invite, create event`() {
                val ownerId = ChannelOwnerId("owner-id")
                val invitingUserId = InvitedUserId("test-inviting-id")
                val limitAt = OffsetDateTime.now().plusDays(1)
                val sut = createSut(
                    owners = mutableListOf(
                        ownerId,
                    ),
                    inviting = mutableMapOf()
                )

                val actual = sut.invite(
                    inviterOwnerId = ownerId,
                    invitedUserId = invitingUserId,
                    limitAt = limitAt
                ).value

                Assertions.assertEquals(sut.id, actual.channelId)
                Assertions.assertEquals(invitingUserId, actual.invitedUserId)
                Assertions.assertEquals(limitAt, actual.limitAt)
            }

            @Test
            fun `If exist invite, create event`() {
                val ownerId = ChannelOwnerId("owner-id")
                val invitingUserId = InvitedUserId("test-inviting-id")
                val limitAt = OffsetDateTime.now().plusDays(1)
                val sut = createSut(
                    owners = mutableListOf(
                        ownerId,
                    ),
                    inviting = mutableMapOf(
                        invitingUserId to Inviting(
                            invitedUserId = invitingUserId,
                            limitAt = limitAt.minusWeeks(1)
                        )
                    )
                )

                val actual = sut.invite(
                    inviterOwnerId = ownerId,
                    invitedUserId = invitingUserId,
                    limitAt = limitAt
                ).value

                Assertions.assertEquals(sut.id, actual.channelId)
                Assertions.assertEquals(invitingUserId, actual.invitedUserId)
                Assertions.assertEquals(limitAt, actual.limitAt)
            }
        }

        @Nested
        inner class Fail {
            @Test
            fun `If not have permission, return error`() {
                val ownerId = ChannelOwnerId("owner-id")
                val invitingUserId = InvitedUserId("test-inviting-id")
                val limitAt = OffsetDateTime.now().plusDays(1)
                val sut = createSut(
                    owners = mutableListOf(),
                    inviting = mutableMapOf()
                )

                val actual = sut.invite(
                    inviterOwnerId = ownerId,
                    invitedUserId = invitingUserId,
                    limitAt = limitAt
                ).error

                Assertions.assertEquals(DomainErrorCode.NotHaveInviteAuthority, actual)
            }

        }
    }

    @Nested
    inner class TestAcceptInvite {
        @Nested
        inner class Success {
            @Test
            fun success() {
                val invitingUserId = InvitedUserId("test-inviting-id")
                val limitAt = OffsetDateTime.now().plusDays(1)
                val sut = createSut(
                    inviting = mutableMapOf(
                        invitingUserId to Inviting(
                            invitedUserId = invitingUserId,
                            limitAt = limitAt
                        )
                    )
                )

                val actual = sut.acceptInvite(
                    invitedUserId = invitingUserId,
                    clock = Clock.systemUTC()
                ).value

                Assertions.assertEquals(sut.id, actual.channelId)
                Assertions.assertEquals(invitingUserId, actual.invitedUserId)
            }
        }

        @Nested
        inner class Fail {

            @Test
            fun `When invite is expired, return error`() {
                val clock = Clock.fixed(
                    OffsetDateTime.now().toInstant(), ZoneOffset.UTC
                )
                val invitingUserId = InvitedUserId("test-inviting-id")
                val limitAt = OffsetDateTime.now(clock).minusNanos(1)
                val sut = createSut(
                    inviting = mutableMapOf(
                        invitingUserId to Inviting(
                            invitedUserId = invitingUserId,
                            limitAt = limitAt
                        )
                    )
                )

                val actual = sut.acceptInvite(
                    invitedUserId = invitingUserId,
                    clock = clock
                ).error

                Assertions.assertEquals(DomainErrorCode.NotInvited, actual)
            }

            @Test
            fun `When invite is not exist, return error`() {
                val clock = Clock.fixed(
                    OffsetDateTime.now().toInstant(), ZoneOffset.UTC
                )
                val invitingUserId = InvitedUserId("test-inviting-id")
                val sut = createSut(
                    inviting = mutableMapOf()
                )

                val actual = sut.acceptInvite(
                    invitedUserId = invitingUserId,
                    clock = clock
                ).error

                Assertions.assertEquals(DomainErrorCode.NotInvited, actual)
            }
        }
    }

    @Nested
    inner class TestHandleInvitedChannel {
        @Test
        fun `Add inviting`() {
            val id = ChannelId("test-channel-id")
            val invitedUserId = InvitedUserId("test-inviting-id")
            val limitAt = OffsetDateTime.now().plusDays(1)
            val event = InvitedChannel(
                channelId = id,
                invitedUserId = invitedUserId,
                limitAt = limitAt
            )

            val sut = createSut(
                id = id
            )
                .also { it.handleInvitedChannel(event) }

            val actual = sut.getInviting(invitedUserId) ?: fail("Not found")
            Assertions.assertEquals(invitedUserId, actual.invitedUserId)
            Assertions.assertEquals(limitAt, actual.limitAt)
        }

        @Test
        fun `When exist, inviting is overwrite`() {
            val id = ChannelId("test-channel-id")
            val invitedUserId = InvitedUserId("test-inviting-id")
            val limitAt = OffsetDateTime.now().plusDays(1)
            val event = InvitedChannel(
                channelId = id,
                invitedUserId = invitedUserId,
                limitAt = limitAt
            )

            val sut = createSut(
                id = id,
                inviting = mutableMapOf(
                    invitedUserId to Inviting(invitedUserId, limitAt.plusDays(1))
                )
            )
                .also { it.handleInvitedChannel(event) }

            val actual = sut.getInviting(invitedUserId) ?: fail("Not found")
            Assertions.assertEquals(invitedUserId, actual.invitedUserId)
            Assertions.assertEquals(limitAt, actual.limitAt)
        }
    }

    @Nested
    inner class TestHandleAcceptedInvite {
        @Test
        fun `When accept remove inviting and add owner`() {
            val id = ChannelId("test-channel-id")
            val invitedUserId = InvitedUserId("test-inviting-id")
            val limitAt = OffsetDateTime.now().plusDays(1)
            val event = AcceptedInvite(
                channelId = id,
                invitedUserId = invitedUserId,
            )

            val sut = createSut(
                id = id,
                owners = mutableListOf(),
                inviting = mutableMapOf(
                    invitedUserId to Inviting(invitedUserId, limitAt.plusDays(1))
                )
            )
                .also { it.handleAcceptedInvite(event) }

            val actualInviting = sut.getInviting(invitedUserId)
            Assertions.assertNull(actualInviting)

            val actualOwners = sut.getOwners()
            Assertions.assertEquals(1, actualOwners.size)
            val actualOwnerId = actualOwners[0]
            Assertions.assertEquals(invitedUserId.value, actualOwnerId.value)
        }

        @Test
        fun `Return error, when not exist`() {
            val id = ChannelId("test-channel-id")
            val invitedUserId = InvitedUserId("test-inviting-id")
            val limitAt = OffsetDateTime.now().plusDays(1)
            val event = AcceptedInvite(
                channelId = id,
                invitedUserId = invitedUserId,
            )

            val sut = createSut(
                id = id,
                inviting = mutableMapOf()
            )
            val actual = sut.handleAcceptedInvite(event).error

            Assertions.assertEquals(DomainErrorCode.NotInvited, actual)
        }
    }

    private fun createSut(
        id: ChannelId = ChannelId("test-channel-id"),
        owners: MutableList<ChannelOwnerId> = mutableListOf(),
        inviting: MutableMap<InvitedUserId, Inviting> = mutableMapOf()
    ): ManageChannel = ManageChannel(
        id = id,
        owners = owners,
        inviting = inviting
    )
}