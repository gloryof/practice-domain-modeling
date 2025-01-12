package jp.glory.channel.usecase

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jp.glory.base.domain.DomainErrorCode
import jp.glory.base.usecase.AuthorizedUserId
import jp.glory.base.usecase.UsecaseErrorCode
import jp.glory.channel.domain.event.ChannelEventListener
import jp.glory.channel.domain.event.InvitedChannel
import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.ChannelOwnerId
import jp.glory.channel.domain.model.InvitedUserId
import jp.glory.channel.domain.model.ManageChannel
import jp.glory.channel.domain.repository.ManageChannelRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset

class InviteOwnerToChannelTest {
    @Nested
    inner class Success {
        @Test
        fun success() {
            val clock: Clock = Clock.fixed(
                OffsetDateTime.now().toInstant(),
                ZoneOffset.UTC
            )
            val now = OffsetDateTime.now(clock)
            val authorizedUserId = AuthorizedUserId("test-owner-id")
            val channelId = ChannelId("test-channel-id")
            val invitedUserId = InvitedUserId("test-invited-user-id")
            val limitAt = now.plusHours(24)

            val managedChannel = ManageChannel(
                id = channelId,
                owners = mutableListOf(ChannelOwnerId(authorizedUserId.value)),
                inviting = mutableMapOf()
            )
            val repository: ManageChannelRepository = mockk()
            every {
                repository.findById(channelId)
            } returns Ok(managedChannel)

            val eventListener: ChannelEventListener = mockk()
            every {
                eventListener.handleInvitedChannel(
                    InvitedChannel(
                        channelId = channelId,
                        invitedUserId = invitedUserId,
                        limitAt = limitAt
                    )
                )
            } returns Ok(Unit)


            val sut = createSut(
                repository = repository,
                eventListener = eventListener,
                clock = clock
            )

            val actual = sut.invite(
                InviteOwnerToChannel.Input(
                    ownerId = authorizedUserId,
                    channelId = channelId.value,
                    invitedUserId = invitedUserId.value
                )
            ).value

            Assertions.assertEquals(channelId.value, actual.channelId)
            Assertions.assertEquals(invitedUserId.value, actual.invitedUserId)
            Assertions.assertEquals(limitAt, actual.limitAt)

            verify {
                eventListener.handleInvitedChannel(
                    InvitedChannel(
                        channelId = channelId,
                        invitedUserId = invitedUserId,
                        limitAt = limitAt
                    )
                )
            }
        }
    }

    @Nested
    inner class Fail {
        @Test
        fun `When error not found channel`() {
            val authorizedUserId = AuthorizedUserId("test-owner-id")
            val channelId = ChannelId("test-channel-id")
            val invitedUserId = InvitedUserId("test-invited-user-id")

            val repository: ManageChannelRepository = mockk()
            every {
                repository.findById(channelId)
            } returns Err(DomainErrorCode.ChannelNotFound)

            val sut = createSut(
                repository = repository,
            )

            val actual = sut.invite(
                InviteOwnerToChannel.Input(
                    ownerId = authorizedUserId,
                    channelId = channelId.value,
                    invitedUserId = invitedUserId.value
                )
            ).error

            Assertions.assertEquals(UsecaseErrorCode.ChannelNotFound, actual)
        }

        @Test
        fun `When error not have authorization`() {
            val now = OffsetDateTime.now()
            val authorizedUserId = AuthorizedUserId("test-owner-id")
            val channelId = ChannelId("test-channel-id")
            val invitedUserId = InvitedUserId("test-invited-user-id")

            val managedChannel = ManageChannel(
                id = channelId,
                owners = mutableListOf(),
                inviting = mutableMapOf()
            )
            val repository: ManageChannelRepository = mockk()
            every {
                repository.findById(channelId)
            } returns Ok(managedChannel)

            val clock: Clock = Clock.fixed(
                now.toInstant(),
                ZoneOffset.UTC
            )

            val sut = createSut(
                repository = repository,
                clock = clock
            )

            val actual = sut.invite(
                InviteOwnerToChannel.Input(
                    ownerId = authorizedUserId,
                    channelId = channelId.value,
                    invitedUserId = invitedUserId.value
                )
            ).error

            Assertions.assertEquals(UsecaseErrorCode.NotHaveInviteAuthority, actual)
        }

        @Test
        fun `When error handle event`() {
            val clock: Clock = Clock.fixed(
                OffsetDateTime.now().toInstant(),
                ZoneOffset.UTC
            )
            val now = OffsetDateTime.now(clock)
            val authorizedUserId = AuthorizedUserId("test-owner-id")
            val channelId = ChannelId("test-channel-id")
            val invitedUserId = InvitedUserId("test-invited-user-id")
            val limitAt = now.plusHours(24)

            val managedChannel = ManageChannel(
                id = channelId,
                owners = mutableListOf(ChannelOwnerId(authorizedUserId.value)),
                inviting = mutableMapOf()
            )
            val repository: ManageChannelRepository = mockk()
            every {
                repository.findById(channelId)
            } returns Ok(managedChannel)

            val eventListener: ChannelEventListener = mockk()
            every {
                eventListener.handleInvitedChannel(
                    InvitedChannel(
                        channelId = channelId,
                        invitedUserId = invitedUserId,
                        limitAt = limitAt
                    )
                )
            } returns Err(DomainErrorCode.Unknown)

            val sut = createSut(
                repository = repository,
                eventListener = eventListener,
                clock = clock
            )

            val actual = sut.invite(
                InviteOwnerToChannel.Input(
                    ownerId = authorizedUserId,
                    channelId = channelId.value,
                    invitedUserId = invitedUserId.value
                )
            ).error

            Assertions.assertEquals(UsecaseErrorCode.Unknown, actual)
        }
    }

    private fun createSut(
        repository: ManageChannelRepository = mockk(),
        eventListener: ChannelEventListener = mockk(),
        clock: Clock = Clock.systemUTC()
    ): InviteOwnerToChannel =
        InviteOwnerToChannel(
            repository = repository,
            eventListener = eventListener,
            clock = clock
        )
}