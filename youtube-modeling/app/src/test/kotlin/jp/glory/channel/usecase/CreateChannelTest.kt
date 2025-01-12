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
import jp.glory.channel.domain.event.CreatedChannel
import jp.glory.channel.domain.model.ChannelId
import jp.glory.channel.domain.model.ChannelIdIdGenerator
import jp.glory.channel.domain.model.ChannelOwnerId
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CreateChannelTest {
    @Test
    fun success() {
        val channelId = ChannelId("test-channel-id")
        val ownerId = ChannelOwnerId("test-owner-id")

        val eventListener: ChannelEventListener = mockk()
        every {
            eventListener.handleCreatedChannel(
                CreatedChannel(
                    channelId = channelId,
                    ownerId = ownerId
                )
            )
        } returns Ok(Unit)

        val channelIdIdGenerator: ChannelIdIdGenerator = mockk()
        every {
            channelIdIdGenerator.generate()
        } returns channelId

        val sut = createSut(
            eventListener = eventListener,
            channelIdIdGenerator = channelIdIdGenerator
        )

        val actual = sut.create(
            CreateChannel.Input(AuthorizedUserId(ownerId.value))
        ).value

        Assertions.assertEquals(channelId.value, actual.channelId)

        verify {
            eventListener.handleCreatedChannel(
                CreatedChannel(
                    channelId = channelId,
                    ownerId = ownerId
                )
            )
        }
    }

    @Test
    fun fail() {
        val channelId = ChannelId("test-channel-id")
        val ownerId = ChannelOwnerId("test-owner-id")

        val eventListener: ChannelEventListener = mockk()
        every {
            eventListener.handleCreatedChannel(
                CreatedChannel(
                    channelId = channelId,
                    ownerId = ownerId
                )
            )
        } returns Err(DomainErrorCode.Unknown)

        val channelIdIdGenerator: ChannelIdIdGenerator = mockk()
        every {
            channelIdIdGenerator.generate()
        } returns channelId

        val sut = createSut(
            eventListener = eventListener,
            channelIdIdGenerator = channelIdIdGenerator
        )

        val actual = sut.create(
            CreateChannel.Input(AuthorizedUserId(ownerId.value))
        ).error

        Assertions.assertEquals(UsecaseErrorCode.NotHaveUploadMovieAuthority, actual)
    }

    private fun createSut(
        eventListener: ChannelEventListener = mockk(),
        channelIdIdGenerator: ChannelIdIdGenerator = mockk()
    ): CreateChannel = CreateChannel(
        eventListener = eventListener,
        channelIdIdGenerator = channelIdIdGenerator
    )
}