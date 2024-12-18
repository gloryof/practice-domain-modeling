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
                    ChanelId("subscribed-id-1"),
                    ChanelId("subscribed-id-2"),
                )
            )

            val sut = createSut(
                id = ChanelId("test-channel-id")
            )

            val actual = sut.subscribe(subscriber).value

            Assertions.assertEquals(actual.chanelId.value, actual.chanelId.value)
            Assertions.assertEquals(actual.subscriberId.value, actual.subscriberId.value)
        }

        @Test
        fun `Throw UsecaseException when subscriber dose not subscribe target`() {
            val sut = createSut(
                id = ChanelId("test-channel-id")
            )

            val subscriber = Subscriber(
                id = SubscriberId("test-subscriber-id"),
                subscribedChannelIds = listOf(sut.id)
            )
            val actual = sut.subscribe(subscriber).error

            Assertions.assertEquals(DomainErrorCode.AlreadyChanelSubscribed, actual)
        }
    }

    private fun createSut(
        id: ChanelId = ChanelId("test-channel-id"),
        title: ChanelTitle = ChanelTitle("test-channel-title"),
        subscriberCount: SubscriberCount = SubscriberCount(10u),
        movieCount: MovieCount = MovieCount(11u)
    ): Channel = Channel(
        id = id,
        title = title,
        subscriberCount = subscriberCount,
        movieCount = movieCount
    )
}