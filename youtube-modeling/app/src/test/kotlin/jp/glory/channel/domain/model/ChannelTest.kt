package jp.glory.channel.domain.model

import jp.glory.base.domain.DomainErrorCode
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ChannelTest {
    @Nested
    inner class TestSubscribe {
        @Test
        fun `Return SubscribedChannel when subscriber dose not subscribe target`() {
            val subscriber = Subscriber(
                id = SubscriberId("test-subscriber-id"),
                subscribedChannelIds = listOf(
                    ChannelId("subscribed-id-1"),
                    ChannelId("subscribed-id-2"),
                )
            )

            val sut = createSut(
                id = ChannelId("test-channel-id")
            )

            val actual = sut.subscribe(subscriber).value

            Assertions.assertEquals(actual.channelId.value, actual.channelId.value)
            Assertions.assertEquals(actual.subscriberId.value, actual.subscriberId.value)
        }

        @Test
        fun `Throw UsecaseException when subscriber dose not subscribe target`() {
            val id = ChannelId("test-channel-id")
            val sut = createSut(
                id = id
            )

            val subscriber = Subscriber(
                id = SubscriberId("test-subscriber-id"),
                subscribedChannelIds = listOf(id)
            )
            val actual = sut.subscribe(subscriber).error

            Assertions.assertEquals(DomainErrorCode.AlreadyChannelSubscribed, actual)
        }
    }

    private fun createSut(
        id: ChannelId = ChannelId("test-channel-id"),
        title: ChannelTitle = ChannelTitle("test-channel-title"),
        subscriberCount: SubscriberCount = SubscriberCount(10u),
        movieCount: MovieCount = MovieCount(11u)
    ): Channel = Channel(
        id = id,
        title = title,
        subscriberCount = subscriberCount,
        movieCount = movieCount
    )
}