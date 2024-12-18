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
import jp.glory.channel.domain.event.SubscribedChannel
import jp.glory.channel.domain.model.ChanelId
import jp.glory.channel.domain.model.ChanelTitle
import jp.glory.channel.domain.model.Channel
import jp.glory.channel.domain.model.MovieCount
import jp.glory.channel.domain.model.Subscriber
import jp.glory.channel.domain.model.SubscriberCount
import jp.glory.channel.domain.model.SubscriberId
import jp.glory.channel.domain.repository.ChannelRepository
import jp.glory.channel.domain.repository.SubscriberRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SubscribeChannelTest {
    @Test
    fun success() {
        val channel = Channel(
            id = ChanelId("test-channel-id"),
            title = ChanelTitle("test-channel-title"),
            subscriberCount = SubscriberCount(1u),
            movieCount = MovieCount(2u)
        )
        val subscriber = Subscriber(
            id = SubscriberId("test-subscriber-id"),
            subscribedChannelIds = emptyList()
        )
        val event = SubscribedChannel(
            chanelId = channel.id,
            subscriberId = subscriber.id
        )
        val channelRepository: ChannelRepository = mockk()
        every {
            channelRepository.findById(channel.id)
        } returns Ok(channel)

        val subscriberRepository: SubscriberRepository = mockk()
        every {
            subscriberRepository.findById(subscriber.id)
        } returns Ok(subscriber)

        val channelEventListener: ChannelEventListener = mockk()
        every {
            channelEventListener.handleSubscribed(event)
        } returns Ok(Unit)

        val sut = createSut(
            channelRepository = channelRepository,
            subscriberRepository = subscriberRepository,
            channelEventListener = channelEventListener
        )

        val input = SubscribeChannel.Input(
            channelId = channel.id.value,
            subscriberId = AuthorizedUserId(subscriber.id.value)
        )
        sut.subscribe(input)

        verify {
            channelEventListener.handleSubscribed(event)
        }
    }


    @Test
    fun `Fail when channel is not found`() {
        val channel = Channel(
            id = ChanelId("test-channel-id"),
            title = ChanelTitle("test-channel-title"),
            subscriberCount = SubscriberCount(1u),
            movieCount = MovieCount(2u)
        )
        val subscriber = Subscriber(
            id = SubscriberId("test-subscriber-id"),
            subscribedChannelIds = listOf(channel.id)
        )
        val channelRepository: ChannelRepository = mockk()
        every {
            channelRepository.findById(channel.id)
        } returns Err(DomainErrorCode.ChannelNotFound)

        val subscriberRepository: SubscriberRepository = mockk()
        every {
            subscriberRepository.findById(subscriber.id)
        } returns Ok(subscriber)

        val sut = createSut(
            channelRepository = channelRepository,
            subscriberRepository = subscriberRepository,
        )

        val input = SubscribeChannel.Input(
            channelId = channel.id.value,
            subscriberId = AuthorizedUserId(subscriber.id.value)
        )
        val actual = sut.subscribe(input).error

        Assertions.assertEquals(UsecaseErrorCode.ChannelNotFound, actual)
    }


    @Test
    fun `Fail when subscriber is not found`() {
        val channel = Channel(
            id = ChanelId("test-channel-id"),
            title = ChanelTitle("test-channel-title"),
            subscriberCount = SubscriberCount(1u),
            movieCount = MovieCount(2u)
        )
        val subscriber = Subscriber(
            id = SubscriberId("test-subscriber-id"),
            subscribedChannelIds = listOf(channel.id)
        )
        val channelRepository: ChannelRepository = mockk()
        every {
            channelRepository.findById(channel.id)
        } returns Ok(channel)

        val subscriberRepository: SubscriberRepository = mockk()
        every {
            subscriberRepository.findById(subscriber.id)
        } returns Err(DomainErrorCode.SubscriberNotFound)

        val sut = createSut(
            channelRepository = channelRepository,
            subscriberRepository = subscriberRepository,
        )


        val input = SubscribeChannel.Input(
            channelId = channel.id.value,
            subscriberId = AuthorizedUserId(subscriber.id.value)
        )
        val actual = sut.subscribe(input).error

        Assertions.assertEquals(UsecaseErrorCode.SubscriberNotFound, actual)
    }

    @Test
    fun `Fail when already subscribed`() {
        val channel = Channel(
            id = ChanelId("test-channel-id"),
            title = ChanelTitle("test-channel-title"),
            subscriberCount = SubscriberCount(1u),
            movieCount = MovieCount(2u)
        )
        val subscriber = Subscriber(
            id = SubscriberId("test-subscriber-id"),
            subscribedChannelIds = listOf(channel.id)
        )
        val channelRepository: ChannelRepository = mockk()
        every {
            channelRepository.findById(channel.id)
        } returns Ok(channel)

        val subscriberRepository: SubscriberRepository = mockk()
        every {
            subscriberRepository.findById(subscriber.id)
        } returns Ok(subscriber)

        val sut = createSut(
            channelRepository = channelRepository,
            subscriberRepository = subscriberRepository,
        )


        val input = SubscribeChannel.Input(
            channelId = channel.id.value,
            subscriberId = AuthorizedUserId(subscriber.id.value)
        )
        val actual = sut.subscribe(input).error

        Assertions.assertEquals(UsecaseErrorCode.AlreadyChanelSubscribed, actual)
    }

    private fun createSut(
        channelRepository: ChannelRepository = mockk(),
        subscriberRepository: SubscriberRepository = mockk(),
        channelEventListener: ChannelEventListener = mockk()
    ): SubscribeChannel =
        SubscribeChannel(
            channelRepository = channelRepository,
            subscriberRepository = subscriberRepository,
            channelEventListener = channelEventListener
        )
}