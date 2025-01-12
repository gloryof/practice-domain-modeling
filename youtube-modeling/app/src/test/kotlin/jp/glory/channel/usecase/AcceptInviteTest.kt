package jp.glory.channel.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.base.domain.DomainErrorCode
import jp.glory.base.usecase.UsecaseErrorCode
import jp.glory.channel.domain.event.AcceptedInvite
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.ChannelOwnerId
import jp.glory.channel.domain.model.InvitedUserId
import jp.glory.channel.domain.model.Inviting
import jp.glory.channel.domain.model.ManageChannel
import jp.glory.channel.domain.repository.ManageChannelRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class AcceptInviteTest {
    @Nested
    inner class Success {
        @Test
        fun success() {
            val ownerId = ChannelOwnerId("test-owner")
            val invitedUserId = InvitedUserId("test-invited-user-id")
            val channelId = ChannelId("test-channel")
            val clock: Clock = Clock.fixed(
                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
            )
            val limitAt = OffsetDateTime.now(clock).plusDays(1)
            val inviting = Inviting(
                invitedUserId = invitedUserId,
                limitAt = limitAt
            )

            val channel = ManageChannel(
                id = channelId,
                owners = mutableListOf(ownerId),
                inviting = mutableMapOf(invitedUserId to inviting)
            )

            val repository: ManageChannelRepository = mockk()
            every {
                repository.findById(channelId)
            } returns Ok(channel)

            val eventListener: ChannelEventListener = mockk()
            every {
                eventListener.handleAcceptedInvite(
                    AcceptedInvite(
                        channelId = channelId,
                        invitedUserId = invitedUserId
                    )
                )
            } returns Ok(Unit)

            val sut = createSut(
                repository = repository,
                eventListener = eventListener,
                clock = clock
            )

            val actual = sut.accept(
                AcceptInvite.Input(
                    channelId = channelId.value,
                    invitedUserId = invitedUserId.value
                )
            ).value

            assertEquals(Unit, actual)

            verify {
                eventListener.handleAcceptedInvite(
                    AcceptedInvite(
                        channelId = channelId,
                        invitedUserId = invitedUserId
                    )
                )
            }
        }
    }

    @Nested
    inner class Fail {

        @Test
        fun `Return error, when not found repository`() {
            val invitedUserId = InvitedUserId("test-invited-user-id")
            val channelId = ChannelId("test-channel")
            val clock: Clock = Clock.fixed(
                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
            )

            val repository: ManageChannelRepository = mockk()
            every {
                repository.findById(channelId)
            } returns Err(DomainErrorCode.ChannelNotFound)

            val sut = createSut(
                repository = repository,
                clock = clock
            )

            val actual = sut.accept(
                AcceptInvite.Input(
                    channelId = channelId.value,
                    invitedUserId = invitedUserId.value
                )
            ).error

            assertEquals(UsecaseErrorCode.ChannelNotFound, actual)
        }

        @Test
        fun `Return error, when not invited`() {
            val ownerId = ChannelOwnerId("test-owner")
            val invitedUserId = InvitedUserId("test-invited-user-id")
            val channelId = ChannelId("test-channel")
            val clock: Clock = Clock.fixed(
                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
            )
            val channel = ManageChannel(
                id = channelId,
                owners = mutableListOf(ownerId),
                inviting = mutableMapOf()
            )

            val repository: ManageChannelRepository = mockk()
            every {
                repository.findById(channelId)
            } returns Ok(channel)

            val sut = createSut(
                repository = repository,
                clock = clock
            )

            val actual = sut.accept(
                AcceptInvite.Input(
                    channelId = channelId.value,
                    invitedUserId = invitedUserId.value
                )
            ).error

            assertEquals(UsecaseErrorCode.NotInvited, actual)
        }

        @Test
        fun `Return error, when invited is expired`() {
            val ownerId = ChannelOwnerId("test-owner")
            val invitedUserId = InvitedUserId("test-invited-user-id")
            val channelId = ChannelId("test-channel")
            val clock: Clock = Clock.fixed(
                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
            )
            val limitAt = OffsetDateTime.now(clock).minusDays(1)
            val inviting = Inviting(
                invitedUserId = invitedUserId,
                limitAt = limitAt
            )

            val channel = ManageChannel(
                id = channelId,
                owners = mutableListOf(ownerId),
                inviting = mutableMapOf(invitedUserId to inviting)
            )

            val repository: ManageChannelRepository = mockk()
            every {
                repository.findById(channelId)
            } returns Ok(channel)

            val sut = createSut(
                repository = repository,
                clock = clock
            )

            val actual = sut.accept(
                AcceptInvite.Input(
                    channelId = channelId.value,
                    invitedUserId = invitedUserId.value
                )
            ).error

            assertEquals(UsecaseErrorCode.NotInvited, actual)
        }

        @Test
        fun `Return error, when event handle is failed`() {
            val ownerId = ChannelOwnerId("test-owner")
            val invitedUserId = InvitedUserId("test-invited-user-id")
            val channelId = ChannelId("test-channel")
            val clock: Clock = Clock.fixed(
                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
            )
            val limitAt = OffsetDateTime.now(clock).plusDays(1)
            val inviting = Inviting(
                invitedUserId = invitedUserId,
                limitAt = limitAt
            )

            val channel = ManageChannel(
                id = channelId,
                owners = mutableListOf(ownerId),
                inviting = mutableMapOf(invitedUserId to inviting)
            )

            val repository: ManageChannelRepository = mockk()
            every {
                repository.findById(channelId)
            } returns Ok(channel)

            val eventListener: ChannelEventListener = mockk()
            every {
                eventListener.handleAcceptedInvite(
                    AcceptedInvite(
                        channelId = channelId,
                        invitedUserId = invitedUserId
                    )
                )
            } returns Err(DomainErrorCode.Unknown)

            val sut = createSut(
                repository = repository,
                eventListener = eventListener,
                clock = clock
            )

            val actual = sut.accept(
                AcceptInvite.Input(
                    channelId = channelId.value,
                    invitedUserId = invitedUserId.value
                )
            ).error

            assertEquals(UsecaseErrorCode.Unknown, actual)
        }
    }

    private fun createSut(
        repository: ManageChannelRepository = mockk(),
        eventListener: ChannelEventListener = mockk(),
        clock: Clock = Clock.systemUTC()
    ): AcceptInvite =
        AcceptInvite(
            repository = repository,
            eventListener = eventListener,
            clock = clock
        )
}